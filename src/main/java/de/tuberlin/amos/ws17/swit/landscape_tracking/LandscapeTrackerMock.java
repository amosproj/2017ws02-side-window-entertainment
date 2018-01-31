package de.tuberlin.amos.ws17.swit.landscape_tracking;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class LandscapeTrackerMock implements LandscapeTracker {

    private boolean isTracking = false;

    private DateTime dateTimeLoadImage = null;
    private BufferedImage currentImage = null;

    @Override
    public BufferedImage getImage() throws IOException {
        if (!isTracking)
            return null;

        if (Seconds.secondsBetween(dateTimeLoadImage, new DateTime()).getSeconds() > 7) {
            loadImage();
        }

        return currentImage;
    }

    @Override
    public void startModule() throws ModuleNotWorkingException {
        isTracking = true;

        loadImage();
    }

    private void loadImage() {
        currentImage = ImageUtils.getRandomTestImage();
        dateTimeLoadImage = new DateTime();
        DebugLog.log("new random image loaded");
    }

    @Override
    public boolean stopModule() {
        isTracking = false;
        return true;
    }

    @Override
    public BufferedImage getModuleImage() {
        return null;
    }

    @Override
    public String getModuleName() {
        return null;
    }
}
