package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;
import de.tuberlin.amos.ws17.swit.information_source.AbstractProvider;

public class AbstractProviderTester {


    public static void main(String[] args) {
        AbstractProvider wp = new AbstractProvider();
        PointOfInterest a = new PointOfInterest("", "post hoc ergo propter hoc", new GpsPosition(5.5, 6.6));
        PointOfInterest b = new PointOfInterest("", "gibberish", new GpsPosition(5.5, 6.6));
        PointOfInterest c = new PointOfInterest("", "ghoti", new GpsPosition(5.5, 6.6));

        try {
            System.out.println(wp.setInfoAndUrl(a).getInformationAbstract());
            System.out.println("\n\n" + wp.setInfoAndUrl(b).getInformationAbstract());
            System.out.println("\n\n" + wp.setInfoAndUrl(c).getInformationAbstract());
        } catch (ServiceNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
