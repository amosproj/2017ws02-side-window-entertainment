package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoi;
import se.walkercrou.places.exception.InvalidRequestException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A Service for retrieving {@link PointOfInterest}s
 */
public interface PoiService<T extends PointOfInterest> {

    List<T> loadPlaceForCircle(GpsPosition center, int radius) throws InvalidRequestException;

    List<T> loadPlaceForCircleAndPoiType(GpsPosition center, int radius, PoiType... types) throws InvalidRequestException;

    void addImages(Collection<T> poisToAddPhotosTo);

}
