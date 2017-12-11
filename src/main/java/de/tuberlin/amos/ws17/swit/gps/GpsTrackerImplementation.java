package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import org.joda.time.DateTime;

import javax.imageio.ImageIO;

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

	public String getModuleName(){ return "GpsModule"; }

	public BufferedImage getModuleImage() {
		String path = "";
		try {
			this.getClass();
			this.getClass().getResource("");
			path = this.getClass().getClassLoader().getResource("module_images/gps_tracker.png").getPath();
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println(path);
		}
		return null;
	}
	// returns an object filled with the current available information.
	// Latitude and longitude are new values, the others may be outdated (but still the most actual)
	// isUpdated() only is true, if latitude and longitude are new
	public KinematicProperties fillDumpObject(KinematicProperties kinProp) throws ModuleNotWorkingException{
		if (portReader.isUpdated()){
			portReader.fillKinematicProperties(kinProp);
			if (kinProp == null)
				throw new ModuleNotWorkingException(); // later: throw noSignalException or something like that
			return kinProp;
		}
		else {
			throw new ModuleNotWorkingException();
		}
	}

	public LinkedList<GpsPosition> getGpsList() {
		if(portReader != null){
			return portReader.getGpsList();
		}
		else return null;
	}

	public void startModule() throws ModuleNotWorkingException{
		if (portReader.start() == false){
			throw new ModuleNotWorkingException();
		}
	}

	public boolean stopModule(){
		portReader = null;
		return true;
	}

	// init for the port mode
	private void initPortMode(){
		portReader = new GpsPortReader();
	}
/*
	// init for the file mode (outdated)
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
