package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerImplementation;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;

import java.awt.image.BufferedImage;

public class LandscapeTrackingDemo {

    public static void main(String[] args) {
        LandscapeTrackerImplementation lt = LandscapeTrackerImplementation.getInstance();
        lt.setNoCameraMode(false);
        try {
            lt.webcam.start();
        } catch (FrameGrabber.Exception e) {
            // e.printStackTrace();
        }
        try {
            CanvasFrame frame = new CanvasFrame("Demo Webcam");
            BufferedImage img;

            // fake stream
            img = lt.getImage();
            do {
                if(img != null) {
                    frame.showImage(img);
                }
                img = lt.getImage();
                Thread.sleep(100);
            } while (frame.isVisible() && img == null);
            //frame.dispose();
            lt.webcam.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
