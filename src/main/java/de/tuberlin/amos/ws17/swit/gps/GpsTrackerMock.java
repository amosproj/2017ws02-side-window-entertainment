package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.KinematicProperties;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;

import org.joda.time.DateTime;

import javax.imageio.ImageIO;

public class GpsTrackerMock extends GpsTrackerImplementation {

    private LinkedList<KinematicProperties> travelledList;

    private double latitude;
    private double longitude;

    // constructor
    public GpsTrackerMock() {
        latitude = 52.516207;
        longitude = 13.377712;

        travelledList = new LinkedList<KinematicProperties>();
    }

    // returns latest gps position from either the file reader or the port reader
    //public GpsPosition getGpsPosition(){
    //    return null;
    //}

    public String getModuleName(){ return "GpsModule"; }

    public BufferedImage getModuleImage() {
        String path = "";
        try {
            path = this.getClass().getClassLoader().getResource("module_images/gps_tracker.png").getPath();
            return ImageIO.read(new File(path));
        } catch (IOException|NullPointerException e) {
            e.printStackTrace();
            System.out.println(path);
        }
        return null;
    }

    // returns an object filled with the fake values (fot testing without hardware)
    // for every new request, a new coordinate is sent
    public KinematicProperties fillDumpObject(KinematicProperties kinProp) {
        DateTime now = new DateTime();

        //get return values
        latitude += 0.000483;
        longitude += 0.0000805;
        Random rand = new Random();
        double course = rand.nextDouble() * 360;
        double velocity = rand.nextDouble() * 100;

        // fill return object
        KinematicProperties obj = new KinematicProperties();
        obj.setLatitude(latitude);
        obj.setLongitude(longitude);
        obj.setTimeStamp(now);
        obj.setCourse(course);
        obj.setVelocity(velocity);

        DebugLog.log(DebugLog.SOURCE_GPS,"Current position: (" + latitude + ", " + longitude + ")");
        return obj;

    }

    public void startModule() {
        // not a real module, does not need starting
    }

    public boolean stopModule(){
        // not a real module, does not need stopping
        return true;
    }


    @Override
    public LinkedList<KinematicProperties> getGpsTrack(int count) {

        if (travelledList.size() <= count) {
            return travelledList;
        } else {
            int start = travelledList.size() - count;
            return new LinkedList<>(travelledList.subList(start, travelledList.size() - 1));
        }
    }
}
