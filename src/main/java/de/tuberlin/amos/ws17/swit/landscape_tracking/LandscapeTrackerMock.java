package de.tuberlin.amos.ws17.swit.landscape_tracking;

import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class LandscapeTrackerMock implements LandscapeTracker {

    boolean isTracking = false;

    @Override
    public BufferedImage getImage() throws IOException {
        if (isTracking)
            ImageUtils.getTestImageFile("brandenburger-tor.jpg");
        return null;
    }

    @Override
    public void startModule() throws ModuleNotWorkingException {
        isTracking = true;
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
