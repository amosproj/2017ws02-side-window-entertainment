package de.tuberlin.amos.ws17.swit.landscape_tracking;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class LandscapeTrackerImplementation implements LandscapeTracker{

    public boolean noCameraMode = false;
    public WebcamImplementation webcamImp;
    JFrame frame=new JFrame();
    private ArrayList<String> examplePicturesNames;
    private String path = ".\\src\\main\\resources\\test_images\\";
    ArrayList<BufferedImage> examplePictures = new ArrayList<>();
    private BufferedImage lastImage;

    public LandscapeTrackerImplementation(WebcamImplementation webcamImplementation) {

        this.webcamImp = webcamImplementation;

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
        if(!webcamImp.getWebcam().isOpen()) webcamImp.getWebcam().open();
        return webcamImp.getWebcam().getImage();
    }
}
