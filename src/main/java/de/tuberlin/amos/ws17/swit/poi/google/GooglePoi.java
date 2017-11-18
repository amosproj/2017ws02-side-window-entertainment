package de.tuberlin.amos.ws17.swit.poi.google;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.tuberlin.amos.ws17.swit.poi.Poi;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import se.walkercrou.places.Place;

import java.util.*;

public class GooglePoi implements Poi {

	private Place p;

	GooglePoi(Place p){
		this.p=p;
	}

	@Override
	public String getId() {
		return p.getPlaceId();
	}

	@Override
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

	public Coordinate getPosition() {
		return new Coordinate(p.getLongitude(), p.getLatitude());
	}

	public Polygon getAreal() {
		return null;
	}

	public PoiSource getPoisource() {
		return PoiSource.googlePlacesApi;
	}

	public String getNameForUser() {
		return p.getName();
	}

	public String getDescriptionForUser() {
		//TODO
		return p.getTypes().toString();
	}

	public String getUrl() {
		return p.getWebsite();
	}

	public Set<GoogleType> getTypes(){
		Set<GoogleType> types= new HashSet<>();

		for(String type: p.getTypes()){

			try {
				types.add(GoogleType.valueOf(type));

			} catch (IllegalArgumentException ex) {
				System.err.println("Enum of places Api does not match current google enums for enum: " +type);
				ex.printStackTrace();
			}
		}


		return types;
	}

	public Place getPlace() {
		return p;
	}

	@Override
	public String toString() {
		return "GooglePoi{" +
				"nameForUser='" + getNameForUser() + '\'' +
				", id=" + getId() +
				", types=" + getPoiTypes().toString() +
				", position=" + getPosition() +
				", areal=" + getAreal() +
				", poisource=" + getPoisource() +
				", descriptionForUser='" + getDescriptionForUser() + '\'' +
				", url='" + getUrl() + '\'' +
				'}';
	}
}
