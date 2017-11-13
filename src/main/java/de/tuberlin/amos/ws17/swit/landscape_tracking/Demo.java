package de.tuberlin.amos.ws17.swit.landscape_tracking;

import org.bytedeco.javacv.CanvasFrame;

import java.awt.image.BufferedImage;

public class Demo {

    public static void main(String[] args) {
        LandscapeTrackerImplementation lt = LandscapeTrackerImplementation.getInstance();
        lt.setNoCameraMode(true);
        try {
            CanvasFrame frame = new CanvasFrame("Demo Webcam");
            BufferedImage img;

            // fake stream
            img = lt.getImage();
            while (frame.isVisible() && img != null) {
                frame.showImage(img);
                img = lt.getImage();
                Thread.sleep(100);
            }
            frame.dispose();
            lt.webcam.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
