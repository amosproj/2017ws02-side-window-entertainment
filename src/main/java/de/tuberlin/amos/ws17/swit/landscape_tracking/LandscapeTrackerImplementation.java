package de.tuberlin.amos.ws17.swit.landscape_tracking;


import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


public class LandscapeTrackerImplementation implements LandscapeTracker{

    public boolean noCameraMode = false;


    public WebcamImplementation webcamImp;
    JFrame frame=new JFrame();
    private ArrayList<String> examplePicturesNames;
    //private String path = ".\\src\\main\\resources\\test_images\\";
    ArrayList<BufferedImage> examplePictures = new ArrayList<>();
    private BufferedImage lastImage;


    public LandscapeTrackerImplementation() {

    }

    public void setWebcamImp(WebcamImplementation webcamImp) {
        this.webcamImp = webcamImp;
    }



    @Override
    public BufferedImage getImage() throws IOException {
        if(!webcamImp.getWebcam().isOpen()) webcamImp.getWebcam().open();
        return webcamImp.getWebcam().getImage();
    }

    @Override
    public void startModule() throws ModuleNotWorkingException {
        WebcamImplementation webcamImplementation = null;
        try {
            webcamImplementation = new WebcamBuilder()
                    .setWebcamName("Webcam Logitech HD Pro Webcam C920 1")
                    .setViewSize(new Dimension(640, 480))
                    .setWebcamDiscoveryTimeout(10000).build();
        } catch (Exception e) {
            throw new ModuleNotWorkingException();
        }
        this.webcamImp = webcamImplementation;
    }

    @Override
    public boolean stopModule() {
        return false;
    }

    @Override
    public BufferedImage getModuleImage() {
        String path = "";
        try {
            this.getClass();
            this.getClass().getResource("");
            path = this.getClass().getClassLoader().getResource("module_images/landscape_tracking.jpg").getPath();
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(path);
        }
        return null;
    }

    @Override
    public String getModuleName() {
        return "Landscape Tracker";
    }
}
