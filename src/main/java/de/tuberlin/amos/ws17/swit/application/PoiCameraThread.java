package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.common.*;

import java.awt.image.BufferedImage;

public class PoiCameraThread extends Thread {

    ApplicationControllerImplementation controller;

    public PoiCameraThread(ApplicationControllerImplementation controller) {
        super();
        this.controller = controller;
    }

    public void run() {
        while(controller.run) {
            //UserPosition userPosition = new UserPosition();
            //TODO @Christian User Position vom User Tracking ermittelt

            BufferedImage image;
            //TODO @JulianL Anfrage an Landscape Tracking mit der userPosition, um Bild zu erhalten

            PointOfInterest poi = new PointOfInterest();
            //TODO @Chinh Anfrage an die Bildanalyse mit Hilfe des aufgenommenen Bildes

            //TODO @JulianL Anfrage an information source mit ermitteltem POI

            controller.addPOI(poi);
        }
    }
}
