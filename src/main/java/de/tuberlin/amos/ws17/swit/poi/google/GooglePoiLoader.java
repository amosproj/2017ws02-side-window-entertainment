package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.poi.PoiLoader;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.RequestHandler;
import se.walkercrou.places.exception.GooglePlacesException;
import se.walkercrou.places.exception.InvalidRequestException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GooglePoiLoader implements PoiLoader<GooglePoi> {

	private GooglePlaces client;
	private RequestHandler rh= new FixedRequestHandler();

	private GoogleTypeMap typeMap= new GoogleTypeMap();

	public GooglePoiLoader(String yourApiKey, boolean enableLogging) {
		client = new GooglePlaces(yourApiKey, rh);
		client.setDebugModeEnabled(enableLogging);
	}

	public List<GooglePoi> loadPlaceForCircle(double lng, double lat, int radius) throws InvalidRequestException{

		return loadPlaceForCircle(lng, lat, radius, new Param[0]);
	}
	public List<GooglePoi> loadPlaceForCircleAndType(double lng, double lat, int radius, GoogleType... types) throws InvalidRequestException{

		Param[] params=new Param[types.length];
		for(int i=0; i<types.length; i++){
			params[i]=new Param("type").value(types[i]);
		}

		return loadPlaceForCircle(lng, lat, radius, params);
	}
	public List<GooglePoi> loadPlaceForCircleAndPoiType(double lng, double lat, int radius, PoiType... types) throws InvalidRequestException{

		Set<GoogleType> gTypes=new HashSet<>();
		for(PoiType type: types){
			gTypes.addAll(typeMap.getKeysByValue(type));
		}

		Param[] params=new Param[gTypes.size()];

		int i=0;
		for(GoogleType gType: gTypes){
			params[i]=new Param("type").value(gType);
			i++;
		}

		return loadPlaceForCircle(lng, lat, radius, params);
	}

	private List<GooglePoi> loadPlaceForCircle(double lng, double lat, int radius, Param[] params) throws InvalidRequestException{

		try {
			List<Place> places = client.getNearbyPlaces(lat, lng, radius, GooglePlaces.MAXIMUM_RESULTS, params);
			places = getPlacesDetails(places);
			return toPoiImpl(places);

		}catch(GooglePlacesException e){
			e.printStackTrace();
			System.err.println(e.getErrorMessage());
			return null;
		}
	}
	private static List<Place> getPlacesDetails(List<Place> places){
		
		List<Place> detailedPlaces=new ArrayList<>();
		
		for(Place p: places){
			String gson="{ \"result\" : "+p.getJson().toString()+"\n}";
			detailedPlaces.add(Place.parseDetails(p.getClient(), gson));
		}
		return detailedPlaces;
		
	}
	
	private static List<GooglePoi> toPoiImpl(List<Place> places){
		
		List<GooglePoi> implList=new ArrayList<>();
		
		for(Place p: places){
			implList.add(new GooglePoi(p));
		}

		return implList;
	}

}
