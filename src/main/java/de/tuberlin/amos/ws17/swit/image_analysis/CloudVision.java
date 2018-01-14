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
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CloudVision implements LandmarkDetector {

    private static final String CLOUD_VISION_API_KEY = ApiConfig.getProperty("CloudVision");

    private static final String LANGUAGE = "de";

    private static final String APPLICATION_NAME = "Swit-Image-Analysis";

    private static final String LANDMARK_DETECTION_FEATURE = "LANDMARK_DETECTION";

    private static final int MAX_RESULTS = 5;

    private static LandmarkDetector instance;

    private final Vision vision;

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

    private List<LandmarkResult> identifyLandmarks(BufferedImage bufferedImage, int maxResults) throws IOException {
        try {
            Image image = ImageUtils.convertToImage(bufferedImage);
            return identifyLandmarks(image, maxResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<PointOfInterest> identifyPOIs(Path path) throws IOException {
        byte[] data = Files.readAllBytes(path);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        return identifyPOIs(ImageIO.read(bais));
    }

    @Override
    public List<PointOfInterest> identifyPOIs(BufferedImage image) {
        try {
            List<LandmarkResult> results = identifyLandmarks(image, MAX_RESULTS);
            return results.stream()
                    .map(CloudVision::convertToPOI)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private static PointOfInterest convertToPOI(LandmarkResult landmark) {
        PointOfInterest poi = new PointOfInterest();
        poi.setId(landmark.getId());
        poi.setName(landmark.getName());
        poi.setGpsPosition(!landmark.getLocations().isEmpty() ? landmark.getLocations().get(0) : null);
        poi.setImage(landmark.getCroppedImage());
        return poi;
    }

    private List<LandmarkResult> identifyLandmarks(Image image, int maxResults) throws IOException {
        if (this.vision == null) {
            System.err.print("Cloud Vision service unavailable.");
            return Collections.emptyList();
        }
        this.bufferedImage = ImageUtils.convertToBufferedImage(image);
        // TODO: reduce image size if necessary
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
        List<LandmarkResult> landmarkResults = convertToLandmarkResults(response.getLandmarkAnnotations());
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

        List<Vertex> vertices = result.getBoundingPoly().getVertices();
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
        if (rect.x < 0 || rect.y < 0) {
            return true;
        } else if (rect.x + rect.width > image.getWidth() || rect.y + rect.getHeight() > image.getHeight()) {
            return true;
        } else {
            return false;
        }
    }
}
