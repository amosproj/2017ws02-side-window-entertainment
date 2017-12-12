package de.tuberlin.amos.ws17.swit.gps;

public class GpsTrackerFactory {
	
	private static GpsTracker gpsTracker = null;
	
	public static GpsTracker GetGpsTracker() {
		if (gpsTracker == null) {
			//gpsTracker = new GpsTrackerImplementation();
			gpsTracker = new GpsTrackerMock();
		}
		
		return gpsTracker;
	}
}
