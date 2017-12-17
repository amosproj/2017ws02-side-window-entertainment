package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import gnu.io.SerialPort;
import gnu.io.CommPortIdentifier;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.ExceptionListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.*;
import org.joda.time.DateTime;
import sun.util.logging.PlatformLogger;

import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.logging.*;


public class GpsPortReader implements SentenceListener{

    private LinkedList<GpsPosition> GpsList;

    private boolean debug = false;
    private GpsPosition latestPosition; // outdated, kept for compatibility reasons
    private boolean running;
    private boolean update;
    private DateTime lastMessageReceived;
    private OwnExceptionListener exceptionListener;
    private static Logger l = Logger.getLogger(GpsPortReader.class.getName());

    private double latitude;
    private double longitude;
    private double course;
    private double velocity;

    public GpsPortReader(){
        update = false;
        running = false;
        lastMessageReceived = null;
        exceptionListener = new OwnExceptionListener();

        // save old PrintStream so that the console remains usable
        PrintStream oldOut = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            String message = "";  // message buffer
            char CR = (char) 13;  // add CR + LF to filter text for new line
            char LF = (char) 10;  // add CR + LF to filter text for new line
            String filter = "--- Started ---" + CR + LF; // message to be filtered out

            // the messages comes byte per byte
            @Override
            public void write(int b) throws java.io.IOException {
                char c = (char) b;
                message += c;
                if (filter.indexOf(message) == 0){
                    // if message = filter, then clear message without clearing it
                    if (message.equals(filter)){
                        message = "";
                    }
                }
                else{
                    // print message and clear it
                    oldOut.print(message);
                    message = "";
                }
            }
        }));
    }

    public LinkedList<GpsPosition> getGpsList() { return GpsList; }

    public GpsPosition getLatestPosition() { return latestPosition; }

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
                    update = true;

                    // fill 'latestPosition'. It is only kept for compatibility
                    if (latestPosition == null){
                        latestPosition = new GpsPosition(555, 666, null);
                    }
                    latestPosition.setLatitude(gga.getPosition().getLatitude());
                    latestPosition.setLongitude(gga.getPosition().getLongitude());

                    // fill up GpsList  ( do we use it anymore?? )
                    if (GpsList == null){
                        GpsList = new LinkedList<GpsPosition>();
                    }
                    Date juDate = new Date();
                    DateTime dt = new DateTime(juDate);
                    GpsList.add(new GpsPosition(gga.getPosition().getLatitude(), gga.getPosition().getLongitude(), dt));

                    // in the future: reset GpsList, if a new day starts (a new element comes in with a timestamp of another day)
                    // ^will be done when I figure out timestamp generation
                    while (GpsList.size() >= 100) {
                        GpsList.removeLast();
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
            if (rmc.isValid()){
                if (debug) System.out.println("----- RMC Sentence -----");
                try{
                    // fill 'latestPosition'. It is only kept for compatibility
                    if (latestPosition != null){
                        latestPosition.setCourse(rmc.getCorrectedCourse());
                    }
                    latestPosition.setCourse(rmc.getCourse());
                    //System.out.println("Position: " + latestPosition.getLatitude() + ", " + latestPosition.getLongitude());
                    //System.out.println("Date: " + rmc.getDate().getDay() + "." + rmc.getDate().getMonth() + "." + rmc.getDate().getYear());
                    //System.out.println("Speed: " + rmc.getSpeed() + " knots");
                    //System.out.println("Course: " + rmc.getCourse());

                    // set course for filling up KinematicProperties object
                    course = rmc.getCourse();
                    if (debug) System.out.println("course updated! (" + course + ")");
                }
                catch (net.sf.marineapi.nmea.parser.DataNotAvailableException e){
                    // do nothing. Broken messages are ok
                }

                // generate Timestamp from separate data: maybe later, when we actually need it
                //net.sf.marineapi.nmea.util.Date date = rmc.getDate();
                //Time time = rmc.getTime();
                //GregorianCalendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDay());
                //latestPosition.setTimeStamp(time);
            }
        }

        // course and speed (interesting: speed in km/h)
        if("VTG".equals(s.getSentenceId())){
            lastMessageReceived = new DateTime();
            VTGSentence vtg = (VTGSentence) s;
            try {
                if (debug) System.out.println("Speed: " + vtg.getSpeedKmh() + "km/h");
                if (debug) System.out.println("Course: " + vtg.getMagneticCourse() + "°");
                if (vtg.isValid()) {
                    if (debug)  System.out.println("----- VTG Sentence -----");



                    // fill 'latestPosition'. It is only kept for compatibility
                    if (latestPosition == null) {
                        latestPosition = new GpsPosition(555, 666, null);
                    }
                    latestPosition.setSpeed(vtg.getSpeedKmh());

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

    public boolean start() throws ModuleNotWorkingException{
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
                    throw new ModuleNotWorkingException();
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

    public static void main(String[] args) {
        new GpsPortReader();
    }
}
