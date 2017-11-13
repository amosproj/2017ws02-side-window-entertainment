package de.tuberlin.amos.ws17.swit.landscape_tracking;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface LandscapeTracker {

    /**
     * make a picture and return it as an image
     * @return - picture
     */
    BufferedImage getImage() throws IOException;

    /**
     * returns last fetched image by the Method getImage()
     * @return - last fetched image
     */
    BufferedImage getLastImage();

}