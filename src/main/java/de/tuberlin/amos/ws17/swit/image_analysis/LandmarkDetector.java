package de.tuberlin.amos.ws17.swit.image_analysis;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface LandmarkDetector {

    /**
     * Identifies a landmark by providing a image path
     * @param imagePath Path to the image file
     * @return List of found POIs on the image
     */

    List<PointOfInterest> identifyPOIs(Path imagePath) throws IOException;

    /**
     * Identify landmarks and convert them to POIs
     * @param image Taken image
     * @return List of found POIs on image
     */
    List<PointOfInterest> identifyPOIs(BufferedImage image);
}
