package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.ApiConfig;
import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.poi.google.FixedRequestHandler;
import de.tuberlin.amos.ws17.swit.poi.google.GoogleType;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GooglePlacesPoiService implements PoiService {

    private String googlePlacesApiBaseUrl = "https://maps.googleapis.com/maps/api/place";

    private GooglePlaces googlePlaces;
    private int imageResolutionX = 300;
    private int imageResolutionY = 300;

    public boolean isServiceAvailable() {
        return true;
        //TODO: das funktioniert nicht
//        try {
//            InetAddress serviceAddress = InetAddress.getByName(googlePlacesApiBaseUrl);
//            try {
//                return serviceAddress.isReachable(1000);
//            } catch (IOException e) {
//                return false;
//            }
//        } catch (UnknownHostException e) {
//            return false;
//        }
    }

    public GooglePlacesPoiService() {

    }

    private void initializeGooglePlaces() {
        if (googlePlaces == null) {
            googlePlaces = new GooglePlaces(ApiConfig.getProperty("GooglePlaces"), new FixedRequestHandler());
            googlePlaces.setDebugModeEnabled(true);
        }
    }

    @Override
    public List<PointOfInterest> getPois(GpsPosition gpsPosition, double radiusInMeter) {
        if (!isServiceAvailable())
            return null;

        if (gpsPosition == null || radiusInMeter < 1)
            return null;

        initializeGooglePlaces();

        List<GoogleType> googleTypes = Arrays.asList(
                GoogleType.casino,
                GoogleType.cemetery,
                GoogleType.church,
                GoogleType.city_hall,
                GoogleType.courthouse,
                GoogleType.embassy, GoogleType.hindu_temple, GoogleType.hospital, GoogleType.library,
                GoogleType.local_government_office,
                GoogleType.plumber,
                GoogleType.stadium,
                GoogleType.rv_park,
                GoogleType.synagogue,
                GoogleType.university,
                GoogleType.zoo);

        String googleTypesAsString = googleTypes.stream()
                .map(googleType -> googleType.toString())
                .collect(Collectors.joining("|"));

        Param[] params = new Param[1];
        params[0] = new Param("type").value(GoogleType.zoo);
        //TODO: man kann gar nicht filter denn in der Doku steht drin, das nach dem ersten Parameter alles weitere ignoriert wird
        List<Place> nearbyPlaces = googlePlaces.getPlacesByRadar(
                gpsPosition.getLatitude(),
                gpsPosition.getLongitude(),
                radiusInMeter,
                params);
        //new Param("type").value(googleTypesAsString));

//        List<Place> nearbyPlaces = googlePlaces.getNearbyPlaces(
//                gpsPosition.getLatitude(),
//                gpsPosition.getLongitude(),
//                radiusInMeter,
//                GooglePlaces.MAXIMUM_RESULTS);
//                //new Param("type").value(googleTypesAsString));

        for (Place place:nearbyPlaces) {

        }
        return null;
    }
}
