package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.application.viewmodel.ApplicationViewModelImplementation;
import de.tuberlin.amos.ws17.swit.common.*;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkResult;

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
            //UserPosition userPosition = new UserPosition();
            //TODO @Christian User Position vom User Tracking ermittelt

            BufferedImage image = null;
            //TODO @JulianL Anfrage an Landscape Tracking mit der userPosition, um Bild zu erhalten
            try {
                image = this.controller.landscapeTracker.getImage();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // no image -> skip
            if (image == null) { continue; }

            PointOfInterest poi = analyzeImage(image);

            // no poi detected -> skip
            if (poi == null) { continue; }

            //TODO @JulianS Anfrage an information source mit ermitteltem POI

            controller.addPOI(poi);
        }
    }

    @Nullable
    private PointOfInterest analyzeImage(BufferedImage image) {
        PointOfInterest poi = null;
        try {
            List<LandmarkResult> results = controller.cloudVision.identifyLandmarks(image, 5);
            // return only first result
            if (!results.isEmpty()) {
                LandmarkResult firstResult = results.get(0);
                poi = new PointOfInterest();
                poi.setId(firstResult.getId());
                poi.setName(firstResult.getName());
                poi.setGpsPosition(!firstResult.getLocations().isEmpty() ? firstResult.getLocations().get(0) : null);
                poi.setImage(firstResult.getCroppedImage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return poi;
    }
}
