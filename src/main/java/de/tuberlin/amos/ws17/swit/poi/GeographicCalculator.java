package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.Vector2D;
/**
 * Geographic calculation utilities
 */
public class GeographicCalculator {


    /**
     * @param targetPosition
     * @param vectorFromSource
     * @return
     */
    private static GpsPosition getRedirectedPosition(GpsPosition targetPosition, Vector2D vectorFromSource){
        double virtualLon=targetPosition.getLongitude()+vectorFromSource.getX();
        double virtualLat=targetPosition.getLatitude()+vectorFromSource.getY();

        return new GpsPosition(virtualLon, virtualLat);
    }

    public static GpsPosition expandByMeters(GpsPosition c1, GpsPosition c2, double metersRange) {

        //diff
        double latDiff=c2.getLatitude()-c1.getLatitude();
        double lonDiff=c2.getLongitude()-c1.getLongitude();

        //relation
        double distance = c1.distanceTo(c2);
        double factor=metersRange/distance;

        //calculate new
        double latC3=latDiff*factor+c2.getLatitude();
        double lonC3=lonDiff*factor+c2.getLongitude();

        return new GpsPosition(lonC3, latC3);
    }
    static GpsPosition expandInverseByMeters(GpsPosition c1, GpsPosition c2, double metersRange) {

        //diff
        double latDiff=c2.getLatitude()-c1.getLatitude();
        double lonDiff=c2.getLongitude()-c1.getLongitude();

        //relation
        double distance = c1.distanceTo(c2);
        double factor=metersRange/distance;

        //calculate new
        double latC3=c2.getLatitude()-latDiff*factor;
        double lonC3=c2.getLongitude()-lonDiff*factor;

        return new GpsPosition(lonC3, latC3);
    }
    static GpsPosition expandNormalByMeters(GpsPosition c1, GpsPosition c2, GpsPosition appliedOn,double metersRange) {

        //diff
        double latDiff=c2.getLatitude()-c1.getLatitude();
        double lonDiff=c2.getLongitude()-c1.getLongitude();

        //relation
        double distance = c1.distanceTo(c2);
        double factor=metersRange/distance*(-1);

        //calculate new
        double latC3=appliedOn.getLatitude()+lonDiff*factor;
        double lonC3=appliedOn.getLongitude()-latDiff*factor;

        return new GpsPosition(lonC3, latC3);
    }
}
