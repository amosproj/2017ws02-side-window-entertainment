package de.tuberlin.amos.ws17.swit.landscape_tracking;

import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;

import java.awt.image.BufferedImage;
import java.io.IOException;
//TODO: delete?
public class DemoLandscape implements LandscapeTracker {

    @Override
    public BufferedImage getImage() throws IOException {
        return null;
    }

    @Override
    public void startModule() throws ModuleNotWorkingException {

    }

    @Override
    public boolean stopModule() {
        return false;
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
