package de.tuberlin.amos.ws17.swit.landscape_tracking;

import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface LandscapeTracker extends Module {

    /**
     * make a picture and return it as an image
     * @return - picture
     */
    BufferedImage getImage() throws IOException;

}