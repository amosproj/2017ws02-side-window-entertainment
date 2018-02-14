package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.KinematicProperties;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

import de.tuberlin.amos.ws17.swit.common.exceptions.GpsModuleNotAvailableException;
import de.tuberlin.amos.ws17.swit.common.exceptions.InformationNotAvailableException;


import javax.imageio.ImageIO;

public class GpsTrackerImplementation implements GpsTracker {

	private GpsPortReader portReader;

	// constructor for port mode
	public GpsTrackerImplementation() {
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
	// throws InformationNotAvailableException, if no new data is available
	public KinematicProperties fillDumpObject(KinematicProperties kinProp) throws InformationNotAvailableException{
		if (portReader.isUpdated()){
			portReader.fillKinematicProperties(kinProp);
			if (kinProp == null){
				DebugLog.log("GPS","No new GPS data available.");
				throw new InformationNotAvailableException();
			}
			return kinProp;
		}
		else {
			DebugLog.log("GPS","No new GPS data available.");
			throw new InformationNotAvailableException();
		}
	}

	// starts the module and throws an exception, if no GPS hardware could be found
	public void startModule() throws GpsModuleNotAvailableException{
		if (portReader.start() == false){
			DebugLog.log(DebugLog.SOURCE_GPS,"No GPS device could be found.");
			throw new GpsModuleNotAvailableException();
		}
		else {
			DebugLog.log(DebugLog.SOURCE_GPS,"GPS module started.");
		}
	}

	public boolean stopModule(){
		portReader = null;
		return true;
	}

	public GpsPosition getCurrentPosition() {
		return portReader.getCurrentPosition();
	}

	public LinkedList<KinematicProperties> getGpsTrack(int count) {
		return portReader.getGpsTrack(count);
	}

	public double getDistanceTravelled() {
		return portReader.getDistanceTravelled();
	}

	public long getTimePassed() {
		return portReader.getTimePassed();
	}
}
