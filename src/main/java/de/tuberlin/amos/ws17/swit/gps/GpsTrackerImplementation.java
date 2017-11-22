package de.tuberlin.amos.ws17.swit.gps;

import java.io.*;
import java.util.LinkedList;

public class GpsTrackerImplementation implements GpsTracker {

	private GpsFileReader fileReader;
	private GpsPortReader portReader;

	// constructor for file mode
	public GpsTrackerImplementation(String fileName) {
		initFileMode(fileName);
	}

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
		if(fileReader != null){
			return fileReader.getLatestPosition();
		}
		return null;
	}

	@Override
	public LinkedList<GpsPosition> getGpsList() {
		if(portReader != null){
			return portReader.getGpsList();
		}
		else return null;
	}

	// init for the port mode
	private void initPortMode(){
		portReader = new GpsPortReader();
	}

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
	}
}
