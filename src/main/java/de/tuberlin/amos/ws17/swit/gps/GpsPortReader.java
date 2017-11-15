package de.tuberlin.amos.ws17.swit.gps;

import gnu.io.SerialPort;
import gnu.io.CommPortIdentifier;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.SentenceValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;


public class GpsPortReader implements SentenceListener{

    public GpsPortReader(){
        init();
    }

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
        System.out.println(event.getSentence());
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
                    for (int i = 0; i < 5; i++) {
                        try {
                            String data = buf.readLine();
                            if (SentenceValidator.isValid(data)) {
                                System.out.println("NMEA data found!");
                                return sp;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    is.close();
                    isr.close();
                    buf.close();
                }
            }
            System.out.println("NMEA data was not found..");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void init(){
        try {
            SerialPort sp = getSerialPort();
            if(sp != null){
                InputStream is = sp.getInputStream();
                SentenceReader sr = new SentenceReader(is);
                sr.addSentenceListener(this);
                sr.start();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GpsPortReader();
    }
}
