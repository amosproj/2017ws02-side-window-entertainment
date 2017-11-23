package de.tuberlin.amos.ws17.swit.image_analysis;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.*;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;
import de.tuberlin.amos.ws17.swit.common.ApiConfig;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CloudVision implements LandmarkDetector {

    private static final String CLOUD_VISION_API_KEY = ApiConfig.getCloudVisionKey();

    private static final String LANGUAGE = ApiConfig.getProperty("language");

    private static final String APPLICATION_NAME = "Swit-Image-Analysis";

    private static final String LANDMARK_DETECTION_FEATURE = "LANDMARK_DETECTION";

    private static final Color[] HIGHLIGHT_COLORS = {Color.red, Color.green, Color.blue, Color.cyan, Color.yellow};

    private static LandmarkDetector instance;

    private final Vision vision;

    private List<LandmarkResult> landmarkResults = Collections.emptyList();

    private BufferedImage bufferedImage;

    @Nullable
    public static LandmarkDetector getInstance() {
        if (instance == null) {
            try {
                instance = new CloudVision(getVisionService());
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }

    private CloudVision(Vision vision) {
        this.vision = vision;
    }

    private static Vision getVisionService() throws IOException, GeneralSecurityException {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)
                .setVisionRequestInitializer(requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    public List<LandmarkResult> identifyLandmarks(BufferedImage bufferedImage, int maxResults) throws IOException {
        try {
            Image image = ImageUtils.convertToImage(bufferedImage);
            return identifyLandmarks(image, maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<LandmarkResult> identifyLandmarks(Path path, int maxResults) throws IOException {
        byte[] data = Files.readAllBytes(path);
        Image image = new Image().encodeContent(data);
        return identifyLandmarks(image, maxResults);
    }

    @Override
    public List<LandmarkResult> identifyLandmarkUrl(String gcsUrl, int maxResults) throws IOException {
        ImageSource imageSource = new ImageSource()
                .setImageUri(gcsUrl);

        Image image = new Image()
                .setSource(imageSource);
        return identifyLandmarks(image, maxResults);
    }

    private List<LandmarkResult> identifyLandmarks(Image image, int maxResults) throws IOException {
        if (this.vision == null) {
            System.err.print("Cloud Vision service unavailable.");
            return Collections.emptyList();
        }
        this.bufferedImage = ImageUtils.convertToBufferedImage(image);
        AnnotateImageRequest request = new AnnotateImageRequest()
                .setImage(image)
                .setFeatures(ImmutableList.of(
                        new Feature()
                                .setType(LANDMARK_DETECTION_FEATURE)
                                .setMaxResults(maxResults)))
                .setImageContext(
                        new ImageContext()
                                .setLanguageHints(ImmutableList.of(LANGUAGE)));

        Vision.Images.Annotate annotate =
                vision.images()
                        .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);

        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getLandmarkAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                            ? response.getError().getMessage()
                            : "Unknown error getting image annotations");
        }
        landmarkResults = convertToLandmarkResults(response.getLandmarkAnnotations());
        return landmarkResults;
    }

    private List<LandmarkResult> convertToLandmarkResults(List<EntityAnnotation> annotations) {
        return annotations.stream()
                .map(LandmarkResult::fromEntityAnnotation)
                .map(this::setCroppedImage)
                .collect(Collectors.toList());
    }

    private LandmarkResult setCroppedImage(LandmarkResult result) {
        float upScale = 2f;

        List<Vertex> vertices = result.getBoundlingPoly().getVertices();
        // early exit
        if (bufferedImage == null || vertices.size() != 4) {
            return result;
        }
        int x = vertices.get(0).getX();
        int y = vertices.get(0).getY();
        int width = vertices.get(1).getX() - x;
        int height = vertices.get(3).getY() - y;
        Rectangle originalRect = new Rectangle(x, y, width, height);

        int growWidth = (int) ((width * upScale - width) / 2);
        int growHeight = (int) ((height * upScale - height) / 2);
        Rectangle growRect = new Rectangle(originalRect);
        growRect.grow(growWidth, growHeight);

        while (rectOutsideOfImage(growRect, bufferedImage)) {
//            System.out.println("Rect to big, resizing");
            upScale -= 0.05;
            growWidth = (int) ((width * upScale - width) / 2);
            growHeight = (int) ((height * upScale - height) / 2);
            growRect = new Rectangle(originalRect);
            growRect.grow(growWidth, growHeight);
        }

        BufferedImage croppedImg = ImageUtils.cropImage(bufferedImage, growRect);
        result.setCroppedImage(croppedImg);
        return result;
    }

    private boolean rectOutsideOfImage(Rectangle rect, BufferedImage image) {
        if (rect.x < 0 || rect.y < 0)  {
            return true;
        } else if (rect.x + rect.width > image.getWidth() || rect.y + rect.getHeight() > image.getHeight()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showHighlightedLandmarks() {
        if (bufferedImage == null) {
            return;
        }
        // Create a graphics context on the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
        JFrame frame = new JFrame();

        int k = 0;
        for (LandmarkResult result : landmarkResults) {
            BoundingPoly bp = result.getBoundlingPoly();
            // Draw on the buffered image
            g2d.setColor(HIGHLIGHT_COLORS[k]);
            g2d.setStroke(new BasicStroke(3));
            int npoints = bp.getVertices().size();
            int xpoints[] = new int[npoints];
            int ypoints[] = new int[npoints];
            int xmin = Integer.MAX_VALUE;
            int ymax = 0;
            for (int i = 0; i < npoints; i++) {
                xpoints[i] = bp.getVertices().get(i).getX();
                ypoints[i] = bp.getVertices().get(i).getY();
                if (ypoints[i] > ymax)
                    ymax = ypoints[i];
                if (xpoints[i] < xmin) {
                    xmin = xpoints[i];
                }
            }

            g2d.drawPolygon(xpoints, ypoints, npoints);
            int xtext = xmin + 10;
            int ytext = ymax - 10;
            g2d.setFont(new Font("Serif", Font.BOLD, 16));
            g2d.drawString(result.getName(), xtext, ytext);
            k++;
        }
        g2d.dispose();

        ImageUtils.showImage(bufferedImage, frame);
    }
}
