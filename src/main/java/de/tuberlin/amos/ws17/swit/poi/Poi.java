package de.tuberlin.amos.ws17.swit.poi;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

import java.util.Set;

public interface Poi {
	
	enum PoiSource{googlePlacesApi,googleCloudVisionApi,OSM};
	
	String getId();
	Set<PoiType> getPoiTypes();
	Coordinate getPosition();
	Polygon getAreal();
	PoiSource getPoisource();
	String getNameForUser();
	String getDescriptionForUser();
	String getUrl();
	
}
