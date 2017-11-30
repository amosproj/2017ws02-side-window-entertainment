package de.tuberlin.amos.ws17.swit.image_analysis;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface LandmarkDetector {

    /**
     * Identifies a landmark using the Google Cloud Vision API by providing an image
     * @param bufferedImage Taken image
     * @param maxResults Maximum number of results
     * @return List of found landmarks on the image
     */
    List<LandmarkResult> identifyLandmarks(BufferedImage bufferedImage, int maxResults) throws IOException;

    /**
     * Identifies a landmark using the Google Cloud Vision API by providing an image
     * @param imagePath Path to the image file
     * @param maxResults Maximum number of results
     * @return List of found landmarks on the image
     */
    List<LandmarkResult> identifyLandmarks(Path imagePath, int maxResults) throws IOException;


    /**
     * Identifies a landmark using the Google Cloud Vision API by providing the Google Cloud Storage (GCS) URL of the image
     * @param gcsUrl GCS URL of the image
     * @param maxResults Maximum number of results
     * @return List of found landmarks on the image
     */
    List<LandmarkResult> identifyLandmarkUrl(String gcsUrl, int maxResults) throws IOException;

    /**
     * Identify landmarks and convert them to POIs
     * @param image Taken image
     * @return List of found POIs on image
     */
    List<PointOfInterest> identifyPOIs(BufferedImage image);
}
