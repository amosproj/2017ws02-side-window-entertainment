package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.gps.GpsPosition;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerImplementation;

public class GpsTrackerImplementationDemo {

    public static void main(String[] args){
        GpsTrackerImplementation tracker = new GpsTrackerImplementation("C:\\file.txt");
        GpsPosition pos = tracker.getGpsPosition();
        if(pos != null)
            System.out.println("Latest position: " + pos.getLatitude() + ", " + pos.getLongitude() + " at " + pos.getTimeStamp());
        else
            System.out.println("Latest position: null");
    }
}
