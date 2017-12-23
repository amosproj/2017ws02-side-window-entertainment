package de.tuberlin.amos.ws17.swit.landscape_tracking;

import com.github.sarxos.webcam.Webcam;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class LandscapeTrackerImplementation implements LandscapeTracker{

    private Webcam logitechC920webcam = null;
    private boolean isTracking = false;

    public LandscapeTrackerImplementation() {

    }

    @Override
    public BufferedImage getImage() throws IOException {
        return logitechC920webcam.getImage();
    }

    @Override
    public void startModule() throws ModuleNotWorkingException {
        if (isTracking)
            stopModule();

        findLogitechC920();
        startLogitechC920();
        isTracking = true;
    }

    private void findLogitechC920() throws ModuleNotWorkingException {
        try {
            List<Webcam> webcams = Webcam.getWebcams(2000);
            for (Webcam webcam: webcams) {
                if (webcam.toString().startsWith("Webcam Logitech HD Pro Webcam C920")) {
                    logitechC920webcam = webcam;
                    break;
                }
            }
        } catch (TimeoutException e) {
            throw new ModuleNotWorkingException(e.getMessage());
        }

        if (logitechC920webcam == null) {
            throw new ModuleNotWorkingException("Logitech Kamera nicht vorhanden.");
        }
    }

    private void startLogitechC920() throws ModuleNotWorkingException {
        if (logitechC920webcam == null) {
            throw new ModuleNotWorkingException("Logitech Kamera nicht vorhanden.");
        }

        if (!logitechC920webcam.isOpen()) {
            Dimension fullHD = new Dimension(1920, 1080);
            Dimension[] customViewSizes = new Dimension[] {
                    fullHD
            };
            logitechC920webcam.setCustomViewSizes(customViewSizes);
            logitechC920webcam.setViewSize(fullHD);
            if (!logitechC920webcam.open()) {
                throw new ModuleNotWorkingException("Logitech Kamera konnte nicht gestartet werden.");
            }
        }
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
