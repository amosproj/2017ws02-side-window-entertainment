package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.exceptions.GpsModuleNotAvailableException;
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

    // local variables used for internal processing
    private boolean running;
    private boolean update;
    private DateTime lastMessageReceived;
    private OwnExceptionListener exceptionListener;

    // local data storage that is read when a position is requested
    private double latitude;
    private double longitude;
    private double course;
    private double velocity;
    private LinkedList<KinematicProperties> gpsTrack;
    private GpsPosition firstPoint;
    private DateTime firstTime;

    // init the reader of GPS data over a COP port
    GpsPortReader(){
        update = false;
        running = false;
        lastMessageReceived = null;
        exceptionListener = new OwnExceptionListener();
        gpsTrack = new LinkedList<>();

        // placeholder for location data
        latitude = -1;
        longitude = -1;
        course = -1;
        velocity = -1;

    }

    // functions that need to be implemented but are not important
    public void readingStarted() {
    }

    public void readingPaused(){
    }

    public void readingStopped(){
    }

    public GpsPosition getCurrentPosition(){
        if (latitude == -1 || longitude == -1)
            return null;
        else{
            GpsPosition pos = new GpsPosition();
            pos.setLatitude(latitude);
            pos.setLongitude(longitude);
            return pos;
        }
    }

    /**
     * fires every time new data is received over the observed port
     * @param event as the event to ...
     */
    public void sentenceRead(SentenceEvent event){
        Sentence s = event.getSentence();

        // Position and Time (not really necessary, since RMC has all the info and more)
        // but RMC fires rather rarely
        if("GGA".equals(s.getSentenceId())){
            lastMessageReceived = new DateTime();
            GGASentence gga = (GGASentence) s;
            if (gga.isValid()){
                try{
                    // set values 'latitude' and 'longitude' and set 'update' to true for filling up KinematicProperties object
                    latitude = gga.getPosition().getLatitude();
                    longitude = gga.getPosition().getLongitude();
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

        // RMC: time and date, position, speed, course. Fires rarely for some reason
        if("RMC".equals(s.getSentenceId())){
            lastMessageReceived = new DateTime();
            RMCSentence rmc = (RMCSentence) s;
            try{
                if (rmc.isValid()){
                    // set 'course' for filling up KinematicProperties object
                    course = rmc.getCourse();
                    DebugLog.log(DebugLog.SOURCE_GPS,"GPS: course updated");
                }
            }
            catch (net.sf.marineapi.nmea.parser.DataNotAvailableException e){
                // do nothing. Broken messages are ok
            }
        }

        // VTG: course and speed (interesting: speed in km/h)
        if("VTG".equals(s.getSentenceId())){
            lastMessageReceived = new DateTime();
            VTGSentence vtg = (VTGSentence) s;
            try {
                if (vtg.isValid()) {
                    // set 'velocity' for filling up KinematicProperties object
                    velocity = vtg.getSpeedKmh();
                    DebugLog.log(DebugLog.SOURCE_GPS,"GPS: speed updated");
                }
            }
            catch(net.sf.marineapi.nmea.parser.DataNotAvailableException e){
                // do nothing. Broken messages are ok
            }
        }
    }

    // finds a serial (COM) port that receives GPS messages
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

                    //System.out.println("Scanning port " + sp.getName());

                    // try each port few times before giving up
                    for (int i = 0; i < 50; i++) {
                        try {
                            String data = buf.readLine();
                            if (SentenceValidator.isValid(data)) {
                                //System.out.println("NMEA data found!");
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
            //System.out.println("NMEA data was not found..");
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return null;
    }

    // getter to find out, if the GPS data (latitude and longitude) were updated
    public boolean isUpdated(){ return update; }

    // starts the port reader
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

    // returns a list with up to [int count] last received GPS points
    public LinkedList<KinematicProperties> getGpsTrack(int count){
        LinkedList<KinematicProperties> list = new LinkedList<>();
        for (int i = 1; (i <= count) || (i <= gpsTrack.size()); i++){
            list.push(gpsTrack.get(gpsTrack.size() - i));
        }

        return list;
    }

    // returns the travelled distance in meters since the first received GPS point
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

    // returns the passed times in milliseconds since the first received GPS point
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
