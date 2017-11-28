package de.tuberlin.amos.ws17.swit.gps;

import org.joda.time.DateTime;

public class GpsPosition {
	public GpsPosition(double latitude, double longitude, DateTime timeStamp) {
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
		speedUpdate = false;
		courseUpdate = false;
	}

	// DateTime from Joda
	private DateTime timeStamp;

	// latitude
	private double latitude;

	// longitude
	private double longitude;

	// course in degrees
	private double course;
	private boolean courseUpdate;

	// speed in km/h
	private double speed;
	private boolean speedUpdate;

	public DateTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(DateTime t) {
		this.timeStamp = t;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double lat) {
		this.latitude = lat;
	}

	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double lon) {
		this.longitude = lon;
	}

	public double getCourse() {
		courseUpdate = false;
		return course;
	}
	public void setCourse(double course) {
		courseUpdate = true;
		this.course = course;
	}

	public boolean getCourseUpdate(){ return courseUpdate; }

	public double getSpeed() {
		speedUpdate = false;
		return speed;
	}
	public void setSpeed(double speed) {
		speedUpdate = true;
		this.speed = speed;
	}

	public boolean getSpeedUpdate(){ return speedUpdate; }

	@Override
	public String toString() {
		return Double.toString(latitude) + ", " + Double.toString(longitude) + " at " + timeStamp.toString();
	}
}
