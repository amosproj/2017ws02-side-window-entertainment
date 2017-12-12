package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;

import java.awt.image.BufferedImage;
import java.io.*;

import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import org.joda.time.DateTime;

import javax.imageio.ImageIO;

public class GpsTrackerMock implements GpsTracker {

    // constructor
    public GpsTrackerMock() {

    }

    // returns latest gps position from either the file reader or the port reader
    public GpsPosition getGpsPosition(){
        return null;
    }

    public String getModuleName(){ return "GpsModule"; }

    public BufferedImage getModuleImage() {
        String path = "";
        try {
            path = this.getClass().getClassLoader().getResource("module_images/gps_tracker.png").getPath();
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(path);
        }
        return null;
    }
    // returns an object filled with the fake values (fot testing without hardware)
    public KinematicProperties fillDumpObject(KinematicProperties kinProp) throws ModuleNotWorkingException{
        kinProp.setCourse(180); // south
        kinProp.setVelocity(1); // 1 m/s or 1 km/h
        kinProp.setLatitude(52.5219184);
        kinProp.setLongitude(13.411026);
        kinProp.setTimeStamp(new DateTime());
        kinProp.setAcceleration(0); // constant speed
        return kinProp;
    }

    public void startModule() throws ModuleNotWorkingException{
        // not a real module, does not need starting
    }

    public boolean stopModule(){
        // not a real module, does not need stopping
        return true;
    }
}
