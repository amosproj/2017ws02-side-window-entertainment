package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;

import java.io.*;
import java.util.LinkedList;
import org.joda.time.DateTime;

public class GpsTrackerImplementation implements GpsTracker {

//	private GpsFileReader fileReader;
	private GpsPortReader portReader;
	private KinematicProperties kinematicProperties;

	// constructor for file mode
//	public GpsTrackerImplementation(String fileName) {
//		initFileMode(fileName);
//	}

	// constructor for port mode
	public GpsTrackerImplementation() {
		initPortMode();
	}

	// returns latest gps position from either the file reader or the port reader
	public GpsPosition getGpsPosition(){
		// port reader is prioritized
		if(portReader != null){
			return portReader.getLatestPosition();
		}
//		if(fileReader != null){
//			return fileReader.getLatestPosition();
//		}
		return null;
	}

	// takes a data dump object and fills it information (pass by reference)
	// may not fill up all the attributes
	// returns true if success, false if not
	public void setDumpObject(KinematicProperties kinProp){
		portReader.setKinematicProperties(kinProp);
	}

	public LinkedList<GpsPosition> getGpsList() {
		if(portReader != null){
			return portReader.getGpsList();
		}
		else return null;
	}

	public void start(){
		portReader.start();
	}

	public void stop(){
		// HOW DO I STOP THIS MADNESS??
	}

	// init for the port mode
	private void initPortMode(){
		portReader = new GpsPortReader();
	}
/*
	// init for the file mode
	private void initFileMode(String fileName) {
		File f = new File(fileName);
		if(f != null){
			try{
				fileReader = new GpsFileReader(f);
			}
			catch (FileNotFoundException e){
				System.out.println("File not found!");
			}
		}
	} */
}
