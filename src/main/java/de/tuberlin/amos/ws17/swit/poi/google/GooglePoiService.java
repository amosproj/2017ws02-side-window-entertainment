package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.ApiConfig;
import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.poi.PoiService;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import se.walkercrou.places.*;
import se.walkercrou.places.exception.GooglePlacesException;
import se.walkercrou.places.exception.InvalidRequestException;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * The GooglePoiService provides the basic functionality of the {@link PoiService} and some additional features.
 * Such as a function to explicitly download the image, what is of importance for saving API requests.
 */
public class GooglePoiService implements PoiService<GooglePoi> {

	private GooglePlaces client;
	private RequestHandler rh= new FixedRequestHandler();

	private GoogleTypeMap typeMap= new GoogleTypeMap();

	private GooglePoiFactory poiFactory = new GooglePoiFactory();

	private int xResolution, yResolution;

	public GooglePoiService(int xResolution, int yResolution) throws ModuleNotWorkingException{
	    this(false, xResolution, yResolution);
	    testFunctionality();
	}

	/**
	 * @param enableLogging is to be set true if the logging of the {@link GooglePlaces} client should output its logging informaiton.
	 * @param xResolution as the x resolution for the downloaded images in pixels
	 * @param yResolution as the y resolution for the downloaded images in pixels
	 * @throws ModuleNotWorkingException in case of failure
	 */
	public GooglePoiService(boolean enableLogging, int xResolution, int yResolution) throws ModuleNotWorkingException{
		client = new GooglePlaces(ApiConfig.getProperty("GooglePlaces"), rh);
		client.setDebugModeEnabled(enableLogging);
		this.xResolution=xResolution;
		this.yResolution=yResolution;
		testFunctionality();
	}


    /**
     * Check if a certain POI can be loaded. No return.
	 * @throws ModuleNotWorkingException is thrown in case of failure
	 */
    private void testFunctionality() throws ModuleNotWorkingException{

        GoogleType zoo=GoogleType.zoo;
        Param[] params = new Param[1];
        params[0] = new Param("type").value(zoo);

        try {
            List<Place> places = client.getNearbyPlaces(52.507506, 13.337977, 500, GooglePlaces.MAXIMUM_RESULTS, params);

            //check an return true
            if(places!=null) {
                if (places.size() > 0){
                    if (places.get(0).getName().contains("Zoo")){
						return;
                    }else {
                        throw new ModuleNotWorkingException("Unexpected result when checking places API functionality for a zoo that has been requested.");
                    }
                }else {
                    throw new ModuleNotWorkingException("Unexpected empty result set when checking places API functionality.");
                }
            }else {
                throw new ModuleNotWorkingException("Unexpected null result when checking places API functionality.");
            }

        }catch(GooglePlacesException e){
            throw new ModuleNotWorkingException("Exception from used API with err msg: "+e.getErrorMessage(), e);
        }

    }

	/**
	 * Retrieve the POIs for a given {@link CircleSearchGeometry}
	 * @param circle the area in which the POIs shell be within
	 * @return a Set of the retrieved POis, can be empty but not null
	 */
	public Set<GooglePoi> loadPlaceForCircleSearchGeometry(CircleSearchGeometry circle){
        Set<GooglePoi> pois=new HashSet<>();

        //if nothing definded load all
        if(circle.getPoiTypes()==null
				&&circle.getGoogletypes()==null){
			pois.addAll(
					loadPlaceForCircle(circle.getCenter(), circle.getRadiusInMeters()));
			return pois;
		}

		if(circle.getPoiTypes()!=null){

			PoiType[] types=circle.getPoiTypes().toArray(new PoiType[circle.getPoiTypes().size()]);
			pois.addAll(
					loadPlaceForCircleAndPoiType(circle.getCenter(), circle.getRadiusInMeters(), types));
		}
		if(circle.getGoogletypes()!=null){

			GoogleType[] types=circle.getGoogletypes().toArray(new GoogleType[circle.getGoogletypes().size()]);
			pois.addAll(
					loadPlaceForCircleAndType(circle.getCenter(), circle.getRadiusInMeters(), types));
		}

		return pois;
    }

	/**
	 * Retrieve the POIs with a circle. Simple method, not differentiating in what kind of POIs to get. Not recommended to use.
	 * @param center as the centerpoint
	 * @param radius as the radius in meters
	 * @return a Set of the retrieved POis, can be empty but not null
	 * @throws InvalidRequestException
	 */
	@Override
	public List<GooglePoi> loadPlaceForCircle(GpsPosition center, int radius) throws InvalidRequestException{

		return loadPlaceForCircle(center, radius, new Param[0]);
	}

	/**
	 * Retrieve the POIs with a circle and the given {@link GoogleType}s
	 * @param center as the centerpoint
	 * @param radius as the radius in meters
	 * @param types as the types to be sent
	 * @return a Set of the retrieved POis, can be empty but not null
	 * @throws InvalidRequestException is thrown in case the request was not correct
	 */
	public List<GooglePoi> loadPlaceForCircleAndType(GpsPosition center, int radius, GoogleType... types) throws InvalidRequestException{

    	if(types.length>0) {

    		//concat the types
			String concatTypes = "";

			for (GoogleType type:types) {
				concatTypes+="|"+type.toString();
			}
			concatTypes=concatTypes.replaceFirst("\\S", "");

			Param[] params = new Param[1];
			params[0] = new Param("type").value(concatTypes);

			return loadPlaceForCircle(center, radius, params);
		}
		return loadPlaceForCircle(center, radius);
	}

	/**
	 * * Retrieve the POIs with a circle and the given {@link PoiType}s
	 * @param center as the centerpoint
	 * @param radius as the radius in meters
	 * @param types  as the types to retrieve
	 * @return a Set of the retrieved POis, can be empty but not null
	 * @throws InvalidRequestException is thrown in case the request was not correct
	 */
	@Override
	public List<GooglePoi> loadPlaceForCircleAndPoiType(GpsPosition center, int radius, PoiType... types) throws InvalidRequestException{

		Set<GoogleType> gTypes=new HashSet<>();
		for(PoiType type: types){
			gTypes.addAll(typeMap.getKeysByValue(type));
		}

		return loadPlaceForCircleAndType(center, radius, gTypes.toArray(new GoogleType[gTypes.size()]));
	}


	/**
	 * A method just in case that using the deprecated way of getting multiple places
	 * as done in loadPlaceForCircleAndType is being deprecated one day.
	 * @param center as the centerpoint
	 * @param radius as the radius in meters
	 * @param params  as the types to retrieve
	 * @return a List of the retrieved POis, can be empty but not null
	 * @throws InvalidRequestException is thrown in case the request was not correct
	 */
	private List<GooglePoi> loadPlacesForEachParam(GpsPosition center, int radius, Param[] params) throws InvalidRequestException{

    	Set<GooglePoi> pois=new HashSet<>();

    	for(Param param: params){
    		Param[] single=new Param[1];
    		single[0]=param;
    		pois.addAll(loadPlaceForCircle(center, radius, single));
		}

		return new ArrayList<>(pois);
	}
	private List<GooglePoi> loadPlaceForCircle(GpsPosition center, int radius, Param[] params) throws InvalidRequestException{

		try {
			List<Place> places = client.getNearbyPlaces(center.getLatitude(), center.getLongitude(), radius, GooglePlaces.MAXIMUM_RESULTS, params);
			places = getPlacesDetails(places);
			return poiFactory.createPOIsfromPlace(places);

		}catch(GooglePlacesException e){
			e.printStackTrace();
			System.err.println(e.getErrorMessage());
			return new ArrayList<>();
		}
	}

	private static List<Place> getPlacesDetails(List<Place> places){

    	if (places == null) {
    		return new ArrayList<>();
		}

		List<Place> detailedPlaces=new ArrayList<>();
		
		for(Place p: places){
			String gson="{ \"result\" : "+p.getJson().toString()+"\n}";
			detailedPlaces.add(Place.parseDetails(p.getClient(), gson));
		}
		return detailedPlaces;
	}

	/**
	 * Download the photos from the Places API for each element of the given Collection. Each download costs one request,
	 * so this is to be done carefully.
	 * @param poisToAddPhotosTo as the POIs where a try of adding a photo is made
	 */
	public void addImages(Collection<GooglePoi> poisToAddPhotosTo){
    	if (poisToAddPhotosTo == null) {
    		return;
		}

		for(GooglePoi poi:poisToAddPhotosTo){
			downloadImage(poi);

		}
	}

	private void downloadImage(GooglePoi poi) {
    	if (poi == null) {
    		return;
		}

		if(poi.getPhotoreference()!=null) {

			System.out.print("Getting image for poi " +poi.getId()+" with ref " +poi.getPhotoreference().getReference()+
					" ..."  );
			Photo photo = poi.getPhotoreference();
			BufferedImage image = photo.download(xResolution, yResolution).getImage();
			poi.setImage(image);

			System.out.println("...image added to poi." );
		}
	}

}
