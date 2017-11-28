package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.common.*;

import java.util.ArrayList;
import java.util.List;

public class PoiMapsThread extends Thread {

    ApplicationControllerImplementation controller;

    public PoiMapsThread(ApplicationControllerImplementation controller) {
        super();
        this.controller = controller;
    }

    public void run() {
        while(controller.run) {
            KinematicProperties kinematicProperties = new KinematicProperties();
            //TODO @Vlad Anfrage an das GPS Modul stellen, welches die GPS Daten zurückgibt

            List<PointOfInterest> pois = new ArrayList<PointOfInterest>();
            //TODO @Leander Anfrage an das POI Modul, welches eine Liste von POIs in der Nähe zurückgibt

            //TODO @JulianL Anfrage an das information source Modul, welches für jeden POI in der Liste die Daten abruft

            for(PointOfInterest poi: pois) {
                controller.addPOI(poi);
            }
        }
    }
}
