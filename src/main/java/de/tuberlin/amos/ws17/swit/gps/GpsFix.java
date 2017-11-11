package de.tuberlin.amos.ws17.swit.gps;

public class GpsFix {
  public GpsFix(double stamp, double lat, double lon) {
    this.stamp = stamp;
    this.lat = lat;
    this.lon = lon;
  }

  /**
   * Seconds from January 1, 1970
   */
  private double stamp;

  /**
   * Latitude
   */
  private double lat;

  /**
   * Longitude
   */
  private double lon;

  public double getStamp() {
    return stamp;
  }

  public double getLat() {
    return lat;
  }

  public double getLon() {
    return lon;
  }
}
