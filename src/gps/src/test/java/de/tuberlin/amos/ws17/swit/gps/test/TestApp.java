package de.tuberlin.amos.ws17.swit.gps.test;

import de.tuberlin.amos.ws17.swit.gps.GpsPosition;
import de.tuberlin.amos.ws17.swit.gps.GpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerFactory;

public class TestApp 
{
    public static void main( String[] args )
    {
    	GpsTracker gpsTracker = GpsTrackerFactory.GetGpsTracker();
    	GpsPosition gpsPosition = gpsTracker.GetGpsPosition();
    	
        System.out.println( "Hello GpsPosition: " + gpsPosition.toString());
    }
}
