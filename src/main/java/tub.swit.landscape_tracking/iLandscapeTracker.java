package tub.swit.landscape_tracking;

import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.awt.image.BufferedImage;

public interface iLandscapeTracker {

    /**
     * make a picture and return it as an image
     * @return - picture
     */
    BufferedImage getImage() throws FrameGrabber.Exception, FrameRecorder.Exception;

    /**
     * returns last fetched image by the Method getImage()
     * @return - last fetched image
     */
    BufferedImage getLastImage();

}