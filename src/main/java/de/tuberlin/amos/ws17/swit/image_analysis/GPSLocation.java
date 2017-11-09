package de.tuberlin.amos.ws17.swit.image_analysis;

import com.google.api.services.vision.v1.model.LocationInfo;

public class GPSLocation {

    private double longitude;
    private double latitude;

    // create and initialize a point with given name and
    // (latitude, longitude) specified in degrees
    public GPSLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // return distance (in meters) between this location and another location
    public float distanceTo(GPSLocation other) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLng = Math.toRadians(other.longitude - this.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(other.latitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (earthRadius * c);
    }


    // return string representation of this point
    public String toString() {
        return "(" + latitude + ", " + longitude + ")";
    }

    public static GPSLocation fromLocationInfo(LocationInfo locationInfo) {
        return new GPSLocation(locationInfo.getLatLng().getLatitude(),
                locationInfo.getLatLng().getLongitude());
    }


    // test client
    public static void main(String[] args) {
        GPSLocation loc1 = new GPSLocation(40.366633, 74.640832);
        GPSLocation loc2 = new GPSLocation(42.443087, 76.488707);
        double distance = loc1.distanceTo(loc2);
        System.out.printf("%f meters from\n", distance);
        System.out.println(loc1 + " to " + loc2);
    }
}