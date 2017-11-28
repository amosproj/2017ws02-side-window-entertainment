package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import gnu.io.SerialPort;
import gnu.io.CommPortIdentifier;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.*;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;


public class GpsPortReader implements SentenceListener{

    private LinkedList<GpsPosition> GpsList;

    private GpsPosition latestPosition;
    private boolean running;
    private KinematicProperties kinematicProperties;

    public GpsPortReader(){
        kinematicProperties = null;
        running = false;
    }

    public LinkedList<GpsPosition> getGpsList() { return GpsList; }

    public GpsPosition getLatestPosition() { return latestPosition; }

    public void readingStarted(){
        System.out.println("--- Started ---");
    }

    public void readingPaused(){
        System.out.println("--- Paused ---");
    }

    public void readingStopped(){
        System.out.println("--- Stopped ---");
    }

    public void sentenceRead(SentenceEvent event){
        Sentence s = event.getSentence();
        // Position and Time (not really necessary, since RMC has all the info and more)
        if("GGA".equals(s.getSentenceId())){
            GGASentence gga = (GGASentence) s;
            System.out.println("----- GGA Sentence -----");
            try{
                //if (latestPosition == null){
                //    latestPosition = new GpsPosition(555, 666, null);
                //}
                //latestPosition.setLatitude(gga.getPosition().getLatitude());
                //latestPosition.setLongitude(gga.getPosition().getLongitude());
                kinematicProperties.setLatitude(gga.getPosition().getLatitude());
                kinematicProperties.setLongitude(gga.getPosition().getLongitude());
                // fill up GpsList
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
                // do nothing
            }
        }

        // time and date, position, speed, course
        if("RMC".equals(s.getSentenceId())){
            RMCSentence rmc = (RMCSentence) s;
            System.out.println("----- RMC Sentence -----");
            try{
                //if (latestPosition != null){
                //    latestPosition.setCourse(rmc.getCorrectedCourse());
                //}
                //System.out.println("Position: " + latestPosition.getLatitude() + ", " + latestPosition.getLongitude());
                //System.out.println("Date: " + rmc.getDate().getDay() + "." + rmc.getDate().getMonth() + "." + rmc.getDate().getYear());
                System.out.println("Speed: " + rmc.getSpeed() + " knots");
                System.out.println("Course: " + rmc.getCourse());
                kinematicProperties.setCourse(rmc.getCourse());
            }
            catch (net.sf.marineapi.nmea.parser.DataNotAvailableException e){
                // do nothing
            }

            // generate Timestamp from separate data: maybe later, when we actually need it
            //net.sf.marineapi.nmea.util.Date date = rmc.getDate();
            //Time time = rmc.getTime();
            //GregorianCalendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDay());
            //latestPosition.setTimeStamp(time);
        }

        // course and speed (interesting: speed in km/h)
        if("VTG".equals(s.getSentenceId())){
            VTGSentence vtg = (VTGSentence) s;
            System.out.println("----- VTG Sentence -----");
            try{
                System.out.println("Speed: " + vtg.getSpeedKmh() + "km/h");
                System.out.println("Course: " + vtg.getMagneticCourse() + "°");
            }
            catch (net.sf.marineapi.nmea.parser.DataNotAvailableException e){
                // do nothing
            }


            //if (latestPosition == null){
            //    latestPosition = new GpsPosition(555, 666, null);
            //}

            //latestPosition.setSpeed(vtg.getSpeedKmh());
            kinematicProperties.setVelocity(vtg.getSpeedKmh());

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

    public boolean start(){
        if (!running){
            try {
                SerialPort sp = getSerialPort();
                if(sp != null){
                    InputStream is = sp.getInputStream();
                    SentenceReader sr = new SentenceReader(is);
                    sr.addSentenceListener(this);
                    sr.start();
                }
                else{
                    return false;
                }
            }
            catch (IOException e){
                //e.printStackTrace();
            }
        }
        return true;
    }

    public void stop(){
        if (running){
            // HOW DO I STOP THIS MADNESS??
        }
        running = false;
    }

    void setKinematicProperties(KinematicProperties kinProp){
        kinematicProperties = kinProp;
    }

    public static void main(String[] args) {
        new GpsPortReader();
    }
}
