package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import se.walkercrou.places.Photo;

import java.util.HashSet;
import java.util.Set;

public class GooglePoi extends PointOfInterest{

	GooglePoi(String id, String name, GpsPosition gpsPosition, Set<GoogleType> types) {
		super(id, name, gpsPosition);
		this.types = types;
	}

	private Set<GoogleType> types= new HashSet<>();
	private Photo photoreference;

	public Set<PoiType> getPoiTypes() {

		GoogleTypeMap map= new GoogleTypeMap();

		Set<PoiType> set= new HashSet<>();
		for(GoogleType gType:this.getTypes()){
			PoiType pt=map.get(gType);
			if(pt!=null)
				set.add(pt);
			else
				set.add(PoiType.NOT_DEFINED);
		}

		return set;
	}

	public Set<GoogleType> getTypes(){

		return types;
	}

	void setPhotoreference(Photo photoreference) {
		this.photoreference = photoreference;
	}

	Photo getPhotoreference() {
		return photoreference;
	}

	void setTypes(Set<GoogleType> types) {
		this.types = types;
	}


}
