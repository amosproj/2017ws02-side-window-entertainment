package de.tuberlin.amos.ws17.swit.gps;

public class GpsPosition {
	public GpsPosition(double latitude, double longitude, long timeStamp) {
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
		speedUpdate = false;
		courseUpdate = false;
	}

	// milliseconds from January 1, 1970
	private long timeStamp;

	// latitude
	private double latitude;

	// longitude
	private double longitude;

<<<<<<< HEAD
	// course in degrees
	private double course;
	private boolean courseUpdate;

	// speed in km/h
	private double speed;
	private boolean speedUpdate;

	public double getTimeStamp() {
=======
	public long getTimeStamp() {
>>>>>>> 0041ac2a01e0570b1db3d4cf19bd3c9339f939be
		return timeStamp;
	}
	public void setTimeStamp(long t) {
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
		return Double.toString(latitude) + ", " + Double.toString(longitude) + " at " + Long.toString(timeStamp);
	}
}
