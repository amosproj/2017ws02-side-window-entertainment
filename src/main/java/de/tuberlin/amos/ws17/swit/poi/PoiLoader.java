package de.tuberlin.amos.ws17.swit.poi;

import java.util.List;


public interface PoiLoader<T extends Poi> {

	 List<T> loadPlaceForCircle(double lng, double lat, int radius);

}
