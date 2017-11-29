package de.tuberlin.amos.ws17.swit.landscape_tracking;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface LandscapeTracker {

    /**
     * make a picture and return it as an image
     * @return - picture
     */
    BufferedImage getImage() throws IOException;

}