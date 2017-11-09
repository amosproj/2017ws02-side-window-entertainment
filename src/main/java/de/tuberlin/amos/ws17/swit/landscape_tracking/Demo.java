package de.tuberlin.amos.ws17.swit.landscape_tracking;

import org.bytedeco.javacv.CanvasFrame;

import java.awt.image.BufferedImage;

public class Demo {

    public static void main(String[] args) {
        LandscapeTracker lt = LandscapeTracker.getInstance();
        try {
            CanvasFrame frame = new CanvasFrame("Demo Webcam");
            BufferedImage img;

            // fake stream
            img = lt.getImage();
            while (frame.isVisible() && img != null) {
                frame.showImage(img);
                img = lt.getImage();;
            }
            frame.dispose();
            lt.webcam.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
