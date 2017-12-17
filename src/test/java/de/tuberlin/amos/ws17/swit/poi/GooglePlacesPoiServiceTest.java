package de.tuberlin.amos.ws17.swit.poi;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.util.List;

public class GooglePlacesPoiServiceTest {
    //TODO: junit4 test erstellen
    public static void main(String[] args) {

        PoiService poiService = new GooglePlacesPoiService();

        GpsPosition gpsPosition = new GpsPosition(52.517949, 13.398325);
        List<PointOfInterest> pois = poiService.getPois(gpsPosition, 500);

        for (PointOfInterest poi:pois) {
            System.out.println(poi.getName());
        }
    }

}
