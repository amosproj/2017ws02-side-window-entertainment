package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoi;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoiLoader;
import de.tuberlin.amos.ws17.swit.poi.google.GoogleType;

import java.util.List;

/**
 * A demonstration of the poi package
 * Created by leand on 16.11.2017.
 */
public class GooglePlacesApiDemo {
    public static void main(String[] args) {

        double tiergartenLng=13.33470991;
        double tiergartenLat=52.5083468;
        double tiergartenLng2=13.33490991;
        double tiergartenLat2=52.5085468;
        GpsPosition tiergarten1= new GpsPosition(tiergartenLng, tiergartenLat);
        GpsPosition tiergarten2= new GpsPosition(tiergartenLng2, tiergartenLat2);

        String GOOGLEPLACESAPIKEY ="yourkey";

        GooglePoiLoader loader=new GooglePoiLoader(GOOGLEPLACESAPIKEY, false);

        List<GooglePoi> pois=loader.loadPlaceForCircleAndType(tiergarten1,
                500,
                GoogleType.zoo,
                GoogleType.art_gallery,
                GoogleType.church,
                GoogleType.aquarium);

        System.out.println(pois.toString());


    }
}