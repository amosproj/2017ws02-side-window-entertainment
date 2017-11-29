package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import se.walkercrou.places.Place;

import java.util.HashSet;
import java.util.Set;

public class GooglePoi {

	//TODO: replace GooglePoi with common PointOfInterest

	private Place p;

	GooglePoi(Place p){
		this.p=p;
	}

	public String getId() {
		return p.getPlaceId();
	}

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

	public GpsPosition getPosition() {
		return new GpsPosition(p.getLongitude(), p.getLatitude());
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
				", descriptionForUser='" + getDescriptionForUser() + '\'' +
				", url='" + getUrl() + '\'' +
				'}';
	}
}
