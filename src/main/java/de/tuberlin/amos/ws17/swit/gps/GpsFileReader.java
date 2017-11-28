package de.tuberlin.amos.ws17.swit.gps;

import java.io.*;


import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.event.SentenceEvent;

/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!  OUTDATED AS HELL !!!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
/*
public class GpsFileReader implements SentenceListener{
    private SentenceReader reader;
    private GpsPosition latestPosition;

    public GpsFileReader(File file) throws FileNotFoundException{
        // create sentence reader and provide input stream
        InputStream stream = new FileInputStream(file);
        reader = new SentenceReader(stream);

        // register self as a listener for GGA sentences
        reader.addSentenceListener(this, SentenceId.GLL);
        reader.start();
    }

    public void readingStarted(){
        System.out.println("-- Started --");
    }

    public void readingPaused(){
        System.out.println("-- Paused --");
    }

    public void readingStopped(){
        System.out.println("-- Stopped --");
    }

    public void sentenceRead(SentenceEvent event){
        // Safe to cast as we are registered only for GLL updates. Could
        // also cast to PositionSentence if interested only in position data.
        // When receiving all sentences without filtering, you should check the
        // sentence type before casting (e.g. with Sentence.getSentenceId()).
        GLLSentence s = (GLLSentence) event.getSentence();

        // Do something with sentence data..
        System.out.println(s.getPosition());
        latestPosition = new GpsPosition(s.getPosition().getLatitude(), s.getPosition().getLatitude(), new java.util.Date().getTime());
    }

    public GpsPosition getLatestPosition(){
        return latestPosition;
    }

    public static void main(String[] args){
        if (args.length != 1) {
            System.out.println("Example usage:\njava FileExample nmea.log");
            System.exit(1);
        }

        try {
            new GpsFileReader(new File(args[0]));
            System.out.println("Running, press CTRL-C to stop..");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
*/
