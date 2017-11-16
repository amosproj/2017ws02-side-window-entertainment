package de.tuberlin.amos.ws17.swit.image_analysis;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.*;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getTestImageFile;

public class CloudVision implements LandmarkDetector {

    private static final String CLOUD_VISION_API_KEY = "YOUR-GOOGLE_API_KEY";

    private static final String APPLICATION_NAME = "Swit-Image-Analysis";

    private static final String LANDMARK_DETECTION_FEATURE = "LANDMARK_DETECTION";

    private static final Color[] HIGHLIGHT_COLORS = {Color.red, Color.green, Color.blue, Color.cyan, Color.yellow};

    private static LandmarkDetector instance;

    private final Vision vision;

    private List<LandmarkResult> landmarkResults = Collections.emptyList();

    private Image image;

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
        this.image = image;
        AnnotateImageRequest request = new AnnotateImageRequest()
                .setImage(image)
                .setFeatures(ImmutableList.of(
                        new Feature()
                                .setType(LANDMARK_DETECTION_FEATURE)
                                .setMaxResults(maxResults)));

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
                .collect(Collectors.toList());
    }

    @Override
    public void showHighlightedLandmarks() {
        BufferedImage img = ImageUtils.convertToBufferedImage(this.image);
        // Create a graphics context on the buffered image
        Graphics2D g2d = img.createGraphics();
        JFrame frame = new JFrame();

        int k = 0;
        for (LandmarkResult result: landmarkResults) {
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

        ImageUtils.showImage(img, frame);
    }

    public static String getLandmark(Path imagePath) throws IOException {
        CloudVision app = null;
        try {
            app = new CloudVision(getVisionService());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        List<LandmarkResult> results = app.identifyLandmarks(imagePath, 5);
        return results.get(0).getName();
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        LandmarkDetector landmarkDetector = getInstance();
        if (landmarkDetector != null) {
            landmarkDetector.identifyLandmarks(getTestImageFile("brandenburger-tor.jpg"), 3);
            landmarkDetector.showHighlightedLandmarks();
        }
    }

    private static void printLandmarks(PrintStream out, List<LandmarkResult> landmarks) {
        for (LandmarkResult result : landmarks) {
            out.printf(
                    "\t%s (score: %.3f)\n",
                    result.getName(),
                    result.getScore());
        }
        if (landmarks.isEmpty()) {
            out.println("\tNo landmarks found.");
        }
    }
}
