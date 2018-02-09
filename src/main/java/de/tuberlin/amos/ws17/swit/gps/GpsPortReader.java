package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.exceptions.GpsModuleNotAvailableException;
import de.tuberlin.amos.ws17.swit.common.exceptions.HardwareNotAvailableException;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import gnu.io.SerialPort;
import gnu.io.CommPortIdentifier;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.*;
import org.joda.time.DateTime;

import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;


public class GpsPortReader implements SentenceListener{


    private boolean debug = false;
    private boolean running;
    private boolean update;
    private DateTime lastMessageReceived;
    private OwnExceptionListener exceptionListener;

    private double latitude;
    private double longitude;
    private double course;
    private double velocity;
    private LinkedList<KinematicProperties> gpsTrack;
    private GpsPosition firstPoint;
    private DateTime firstTime;

    public GpsPortReader(){
        update = false;
        running = false;
        lastMessageReceived = null;
        exceptionListener = new OwnExceptionListener();
        gpsTrack = new LinkedList<>();

        // filler
        latitude = -1;
        longitude = -1;
        course = -1;
        velocity = -1;

    }

    public void readingStarted() {
        if (debug) System.out.println("--- Started ---");
    }

    public void readingPaused(){
        if (debug) System.out.println("--- Paused ---");
    }

    public void readingStopped(){
        if (debug) System.out.println("--- Stopped ---");
    }

    public void sentenceRead(SentenceEvent event){
        Sentence s = event.getSentence();
        //System.out.println(s.toString());
        // Position and Time (not really necessary, since RMC has all the info and more)
        if("GGA".equals(s.getSentenceId())){
            lastMessageReceived = new DateTime();
            GGASentence gga = (GGASentence) s;
            if (gga.isValid()){
                if (debug) System.out.println("----- GGA Sentence -----");
                try{
                    // set values 'latitude' and 'longitude' and set 'update' to true for filling up KinematicProperties object
                    latitude = gga.getPosition().getLatitude();
                    if (debug) System.out.println("latitude updated! (" + latitude + ")");
                    longitude = gga.getPosition().getLongitude();
                    if (debug) System.out.println("longitude updated! (" + longitude + ")");
                    DebugLog.log(DebugLog.SOURCE_GPS,"GPS: coordinate updated: " + latitude + ", " + longitude);
                    update = true;

                    // fill first GpsPosition, if it's empty
                    if(firstPoint == null && firstTime == null){
                        firstPoint = new GpsPosition();
                        firstPoint.setLatitude(latitude);
                        firstPoint.setLongitude(longitude);
                        firstTime = new DateTime();
                    }

                    // create KinematicProperties object for GpsTrack
                    KinematicProperties obj = new KinematicProperties();
                    obj.setTimeStamp(lastMessageReceived);
                    obj.setLongitude(longitude);
                    obj.setLatitude(latitude);
                    obj.setVelocity(velocity);
                    obj.setCourse(course);
                    gpsTrack.push(obj);

                    // not sure, if too old elements need to be removed. We want the whole drive to be accessible
                    if (gpsTrack.size() > 1000){
                        gpsTrack.remove(0);
                    }

                }
                catch (net.sf.marineapi.nmea.parser.DataNotAvailableException e) {
                    // do nothing. Broken messages are ok
                }
            }
        }

        // time and date, position, speed, course
        if("RMC".equals(s.getSentenceId())){
            lastMessageReceived = new DateTime();
            RMCSentence rmc = (RMCSentence) s;
            try{
                if (rmc.isValid()){
                    if (debug) System.out.println("----- RMC Sentence -----");
                    // set 'course' for filling up KinematicProperties object
                    course = rmc.getCourse();
                    if (debug) System.out.println("course updated! (" + course + ")");
                    DebugLog.log(DebugLog.SOURCE_GPS,"GPS: course ");
                }
            }
            catch (net.sf.marineapi.nmea.parser.DataNotAvailableException e){
                // do nothing. Broken messages are ok
            }
        }

        // course and speed (interesting: speed in km/h)
        if("VTG".equals(s.getSentenceId())){
            lastMessageReceived = new DateTime();
            VTGSentence vtg = (VTGSentence) s;
            try {
                if (vtg.isValid()) {
                    if (debug)  System.out.println("----- VTG Sentence -----");
                    // set 'velocity' for filling up KinematicProperties object
                    velocity = vtg.getSpeedKmh();
                    if (debug) System.out.println("velocity updated! (" + velocity + ")");
                }
            }
            catch(net.sf.marineapi.nmea.parser.DataNotAvailableException e){
                // do nothing. Broken messages are ok
            }
        }
    }

    private SerialPort getSerialPort(){
        try {
            Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();

            while (e.hasMoreElements()) {
                CommPortIdentifier id = (CommPortIdentifier) e.nextElement();

                if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {

                    SerialPort sp = (SerialPort) id.open("SerialExample", 30);

                    sp.setSerialPortParams(4800, SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    InputStream is = sp.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader buf = new BufferedReader(isr);

                    System.out.println("Scanning port " + sp.getName());

                    // try each port few times before giving up
                    for (int i = 0; i < 50; i++) {
                        try {
                            String data = buf.readLine();
                            if (SentenceValidator.isValid(data)) {
                                System.out.println("NMEA data found!");
                                return sp;
                            }
                        } catch (Exception ex) {
                            //ex.printStackTrace();
                        }
                    }
                    is.close();
                    isr.close();
                    buf.close();
                }
            }
            System.out.println("NMEA data was not found..");
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return null;
    }

    public boolean isUpdated(){ return update; }

    public boolean start() throws GpsModuleNotAvailableException{
        if (!running){
            try {
                SerialPort sp = getSerialPort();
                if(sp != null){
                    InputStream is = sp.getInputStream();
                    SentenceReader sr = new SentenceReader(is);
                    sr.addSentenceListener(this);
                    sr.setExceptionListener(exceptionListener);
                    sr.start();
                }
                else{
                    throw new GpsModuleNotAvailableException();
                }
            }
            catch (IOException e){
                //e.printStackTrace();
            }
        }
        return true;
    }

    // returns an object filled with the current available information.
    // Latitude and longitude are new values, the others may be outdated (but still the most actual)
    KinematicProperties fillKinematicProperties(KinematicProperties kinProp){
        DateTime now = new DateTime();
        if ((lastMessageReceived == null) || (now.getMillis() - lastMessageReceived.getMillis() > 15000)){ // no new message for 15 seconds
            return null;
        }
        kinProp.setLatitude(latitude);
        kinProp.setLongitude(longitude);
        kinProp.setVelocity(velocity);
        kinProp.setCourse(course);
        update = false;


        return kinProp;
    }

    public LinkedList<KinematicProperties> getGpsTrack(int count){
        LinkedList<KinematicProperties> list = new LinkedList<>();
        for (int i = 1; (i <= count) || (i <= gpsTrack.size()); i++){
            list.push(gpsTrack.get(gpsTrack.size() - i));
        }

        return list;
    }

    public double getDistanceTravelled(){
        if (latitude != -1 && longitude != -1 && firstPoint != null){
            GpsPosition currentPos = new GpsPosition();
            currentPos.setLongitude(longitude);
            currentPos.setLatitude(latitude);
            return firstPoint.distanceTo(currentPos);
        }
        else
            return -1;
    }

    public long getTimePassed(){
        if (firstTime != null){
            DateTime now = new DateTime();
            return  now.getMillis() - firstTime.getMillis();
        }
        return -1;
    }

    public static void main(String[] args) {
        new GpsPortReader();
    }
}
