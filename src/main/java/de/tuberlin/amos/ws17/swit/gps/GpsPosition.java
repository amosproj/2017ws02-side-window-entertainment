package de.tuberlin.amos.ws17.swit.gps;

public class GpsPosition {
	
	private double x;
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	
	private double y;
	
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	public GpsPosition(double x, double y) {
		setX(x);
		setY(y);
	}
	
	@Override
	public String toString() {
		return Double.toString(getX()) + ";" + Double.toString(getY());
	}
}
