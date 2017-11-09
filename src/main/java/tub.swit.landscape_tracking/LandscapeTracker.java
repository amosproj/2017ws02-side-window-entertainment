package tub.swit.landscape_tracking;


import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class LandscapeTracker implements iLandscapeTracker{
    private static LandscapeTracker instance = new LandscapeTracker();
    JFrame frame=new JFrame();
    Webcam webcam = new Webcam(0);

    public static LandscapeTracker getInstance() {
        return instance;
    }

    private LandscapeTracker() {
        try {
            webcam.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage lastImage;

    @Override
    public BufferedImage getImage() throws FrameGrabber.Exception, FrameRecorder.Exception {
        lastImage = webcam.takePhoto();
        return lastImage;
    }

    @Override
    public BufferedImage getLastImage() {
        return lastImage;
    }

    public void showImage() {

        BufferedImage img = null;
        try {
            img = getImage();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
        }

        ImageIcon icon = new ImageIcon(img);

        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(),img.getHeight());
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }


    public static void main(String[] args) {
        LandscapeTracker l = LandscapeTracker.getInstance();
        l.showImage();
    }
}
