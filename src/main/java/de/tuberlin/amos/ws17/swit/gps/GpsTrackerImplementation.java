package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.KinematicProperties;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;


import javax.imageio.ImageIO;

public class GpsTrackerImplementation implements GpsTracker {

//	private GpsFileReader fileReader;
	private GpsPortReader portReader;

	// constructor for port mode
	public GpsTrackerImplementation() {
		// start the port reader
		portReader = new GpsPortReader();
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
	// Latitude and longitude are always updated values, the others may be outdated (but still the most actual)
	// isUpdated() only is true, if latitude and longitude are new
	// throws ModuleNotWorkingException, if no new data is available
	public KinematicProperties fillDumpObject(KinematicProperties kinProp) throws ModuleNotWorkingException{
		if (portReader.isUpdated()){
			portReader.fillKinematicProperties(kinProp);
			if (kinProp == null){
				DebugLog.log("No new GPS data available.");
				throw new ModuleNotWorkingException(); // later: throw noSignalException or something like that
			}
			return kinProp;
		}
		else {
			DebugLog.log("No new GPS data available.");
			throw new ModuleNotWorkingException();
		}
	}

	public void startModule() throws ModuleNotWorkingException{
		if (portReader.start() == false){
			DebugLog.log("No GPS device could be found.");
			throw new ModuleNotWorkingException();
		}
	}

	public boolean stopModule(){
		portReader = null;
		return true;
	}

	@Override
	public LinkedList<KinematicProperties> getGpsTrack(int count) {
		return portReader.getGpsTrack(count);
	}
}
