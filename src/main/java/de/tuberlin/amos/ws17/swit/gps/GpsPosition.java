package de.tuberlin.amos.ws17.swit.gps;

public class GpsPosition {
	public GpsPosition(double latitude, double longitude, long timeStamp) {
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Milliseconds from January 1, 1970
	 */
	private long timeStamp;

	/**
	 * Latitude
	 */
	private double latitude;

	/**
	 * Longitude
	 */
	private double longitude;

	public double getTimeStamp() {
		return timeStamp;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return Double.toString(latitude) + ", " + Double.toString(longitude) + " at " + Long.toString(timeStamp);
	}
}
