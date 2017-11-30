package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.application.viewmodel.ApplicationViewModelImplementation;
import de.tuberlin.amos.ws17.swit.common.*;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkResult;
import org.apache.jena.base.Sys;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class PoiCameraThread extends Thread {

    ApplicationViewModelImplementation controller;

    public PoiCameraThread(ApplicationViewModelImplementation controller) {
        super();
        this.controller = controller;
    }

    public void run() {
        while(controller.run) {
            //TODO ACHTUNG!!! DIESER THREAD WIRD NICHT MEHR VERWENDET UND IST DAHER DEPRECATED

            //UserPosition userPosition = new UserPosition();
            BufferedImage image = null;
            try {
                image = this.controller.landscapeTracker.getImage();
            } catch (IOException e) {
                e.printStackTrace();
            }

            image = ImageUtils.getRandomTestImage();

            // no image -> skip
            if (image == null) { continue; }

            List<PointOfInterest> pois = controller.cloudVision.identifyPOIs(image);

            // no poi detected -> skip
            if (pois.isEmpty()) { continue; }

            for (PointOfInterest poi: pois) {
                controller.addPOI(poi);
            }
            break;
        }
    }

}
