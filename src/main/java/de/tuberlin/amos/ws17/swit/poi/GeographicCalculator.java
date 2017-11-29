package de.tuberlin.amos.ws17.swit.poi;

import com.vividsolutions.jts.geom.Coordinate;
import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.Vector2D;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoi;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * Geographic calculation utilities
 */
public class GeographicCalculator {



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
}
