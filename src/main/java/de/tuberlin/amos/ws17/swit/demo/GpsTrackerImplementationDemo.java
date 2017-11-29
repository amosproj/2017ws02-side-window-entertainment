package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.gps.GpsPosition;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerImplementation;

import java.util.LinkedList;

// OUDATED: DOES NOT FOLLOW NEW FORMAT WITH SEPARATE START() AND STORING DATA IN A KINEMATICPROPERTIES OBJECT

public class GpsTrackerImplementationDemo {

    public static void main(String[] args){
        GpsTrackerImplementation tracker = new GpsTrackerImplementation();
        try{
            int i = 0;
            while(i < 30){
                i++;
                Thread.sleep(1000);
                GpsPosition pos = tracker.getGpsPosition();
                if(pos != null)
                    System.out.println("Latest position: " + pos.getLatitude() + ", " + pos.getLongitude()
                            + " at " + pos.getTimeStamp()+ " with speed: " + pos.getSpeed() + " and course : " + pos.getCourse());
                else
                    System.out.println("Latest position: null");
            }

            LinkedList<GpsPosition> gpsList = tracker.getGpsList();
            if(gpsList != null){
                System.out.println("gpsList start:");
                while(gpsList.size() > 0){
                    GpsPosition element = gpsList.pop();
                    System.out.println("Latest position: " + element.getLatitude() + ", " + element.getLongitude() + " at "
                            + element.getTimeStamp() + " with speed: " + element.getSpeed() + " and course: " + element.getCourse());
                }
                System.out.println("gpsList end");
            }
            else{
                System.out.println("gpsList is empty");
            }

        }
        catch(java.lang.InterruptedException e){
            System.out.println("InterruptedEx exception");
        }
    }

}
