package tub.swit.landscape_tracking;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class LandscapeTracker implements iLandscapeTracker{
    private static LandscapeTracker instance = new LandscapeTracker();

    public static LandscapeTracker getInstance() {
        return instance;
    }

    private LandscapeTracker() {

    }

    private BufferedImage lastImage;

    @Override
    public BufferedImage getImage() throws FrameGrabber.Exception, FrameRecorder.Exception {
        Webcam webcam = new Webcam();
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
        JFrame frame=new JFrame();
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
