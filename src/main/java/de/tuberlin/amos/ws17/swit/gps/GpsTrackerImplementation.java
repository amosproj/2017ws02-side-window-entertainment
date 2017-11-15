package de.tuberlin.amos.ws17.swit.gps;

import java.io.*;

class GpsTrackerImplementation implements GpsTracker {
	
	//private GpsPosition latestPosition;
	private GpsFileReader fileReader;

	// constructor for file mode
	public GpsTrackerImplementation(String fileName) {
		initFileMode(fileName);
	}

	// constructor for port mode
	public GpsTrackerImplementation() {
		// initPortMode() not implemented yet
	}

	// returns latest gps position from either the file reader or the port reader
	public GpsPosition getGpsPosition(){
		if(fileReader != null){
			return fileReader.getLatestPosition();
		}
		else
			return new GpsPosition(1,2,3); // fake position
		/* not implemented yet
		if(portReader != null){
			return portReader.getLatestPosition();
		}
		*/
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
