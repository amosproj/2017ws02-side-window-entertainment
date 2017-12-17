package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.util.List;

public interface PoiService {
    List<PointOfInterest> getPois(GpsPosition gpsPosition, double radiusInMeter);
}
