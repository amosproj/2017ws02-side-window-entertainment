package de.tuberlin.amos.ws17.swit.landscape_tracking;


import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class LandscapeTrackerImplementation implements LandscapeTracker{

    public boolean noCameraMode = false;

    private static LandscapeTrackerImplementation instance = new LandscapeTrackerImplementation();
    JFrame frame=new JFrame();
    public Webcam webcam = new Webcam(0);
    private ArrayList<String> examplePicturesNames;
    private String path = ".\\src\\main\\resources\\test_images\\";
    ArrayList<BufferedImage> examplePictures = new ArrayList<>();
    private BufferedImage lastImage;

    public static LandscapeTrackerImplementation getInstance() {
        return instance;
    }

    private LandscapeTrackerImplementation() {
        try {
            webcam.start();
        } catch (FrameGrabber.Exception e) {
            System.out.println("Fehler beim Laden der Kamera! \n Wechsel zu noCameraMode!");
            setNoCameraMode(true);
        }

        if(!webcam.running) {
            setNoCameraMode(true);
        }

        examplePicturesNames = new ArrayList<>();
        examplePicturesNames.add("berliner-dom.jpg");
        examplePicturesNames.add("brandenburger-tor.jpg");
        examplePicturesNames.add("brandenburger-tor-2.jpg");
        examplePicturesNames.add("fernsehturm.jpg");
        examplePicturesNames.add("fernsehturm-2.jpg");
        examplePicturesNames.add("sieges-saeule.jpg");

        examplePicturesNames.forEach(name -> {
            try {
                examplePictures.add(ImageIO.read(new File(path + name)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public BufferedImage getImage() throws IOException {
        if(noCameraMode || !webcam.running) {
            int rand = (int) Math.floor(Math.random()*6);
            lastImage = examplePictures.get(rand);
        }
        else {
            lastImage = webcam.takePhoto();
        }
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
        } catch (IOException e) {
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

    public void setNoCameraMode(boolean noCameraMode) {
        if(noCameraMode && webcam.running) {
            try {
                webcam.stop();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
        this.noCameraMode = noCameraMode;
    }
}
