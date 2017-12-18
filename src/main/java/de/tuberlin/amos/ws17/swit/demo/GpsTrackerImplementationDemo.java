package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerImplementation;

// OUDATED: DOES NOT FOLLOW NEW FORMAT WITH SEPARATE START() AND STORING DATA IN A KINEMATICPROPERTIES OBJECT

public class GpsTrackerImplementationDemo {

    public static void main(String[] args){
        // choice: real gps module or fake one
        //GpsTrackerImplementation tracker = new GpsTrackerMock();
        GpsTrackerImplementation tracker = new GpsTrackerImplementation();
        try{
            tracker.startModule();
            KinematicProperties prop = new KinematicProperties();
            while (true){
                prop = tracker.fillDumpObject(prop);
                String timeString = prop.getTimeStamp().getHourOfDay() + ":" + prop.getTimeStamp().getMinuteOfHour() + ":" + prop.getTimeStamp().getSecondOfMinute();
                System.out.println("-----------------------------");
                System.out.println("latitude: " + prop.getLatitude());
                System.out.println("longitude: " + prop.getLongitude());
                System.out.println("time: " + timeString);
                System.out.println("velocity: " + prop.getVelocity());
                System.out.println("acceleration: " + prop.getAcceleration());
                System.out.println("course: " + prop.getCourse());
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e){
                    System.out.println("Interrupted");
                    break;
                }
            }
        }
        catch (ModuleNotWorkingException e){
            System.out.println("Module not working exception");
        }
        //try{


            //}
        //catch(java.lang.InterruptedException e){
            //    System.out.println("InterruptedEx exception");
        //}
    }

}
