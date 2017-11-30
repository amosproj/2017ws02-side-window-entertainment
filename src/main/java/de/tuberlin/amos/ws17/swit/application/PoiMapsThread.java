package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.application.viewmodel.ApplicationViewModelImplementation;
import de.tuberlin.amos.ws17.swit.common.*;

import java.util.ArrayList;
import java.util.List;

public class PoiMapsThread extends Thread {

    ApplicationViewModelImplementation controller;

    public PoiMapsThread(ApplicationViewModelImplementation controller) {
        super();
        this.controller = controller;
    }

    public void run() {
        while(controller.run) {
            //TODO ACHTUNG!!! DIESER THREAD WIRD NICHT MEHR VERWENDET UND IST DAHER DEPRECATED

            KinematicProperties kinematicProperties = new KinematicProperties();
            controller.gpsTracker.setDumpObject(kinematicProperties);


            List<PointOfInterest> pois = new ArrayList<PointOfInterest>();


            for(PointOfInterest poi: pois) {
                controller.addPOI(poi);
            }
        }
    }
}
