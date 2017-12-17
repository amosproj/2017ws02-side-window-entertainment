package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoi;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoiService;
import de.tuberlin.amos.ws17.swit.poi.google.MultiCircleSearchGeometry;
import de.tuberlin.amos.ws17.swit.poi.google.SearchGeometryFactory;

import java.util.Set;

/**
 * A demonstration of the poi package
 * Created by leand on 16.11.2017.
 */
public class LoadPlacesInDirectionDemo {
    public static void main(String[] args) {


        //the coordintes
        double tiergartenLng=13.33470991;
        double tiergartenLat=52.5083468;
        double tiergartenLng2=13.33490991;
        double tiergartenLat2=52.5085468;
        GpsPosition tiergarten1= new GpsPosition(tiergartenLng, tiergartenLat);
        GpsPosition tiergarten2= new GpsPosition(tiergartenLng2, tiergartenLat2);
        try{
            GooglePoiService loader=new GooglePoiService(100, 100);

            SearchGeometryFactory searchGeometryFactory=new SearchGeometryFactory(2.1, 200, 3, null, null);
            MultiCircleSearchGeometry searchGeometry=searchGeometryFactory.createSearchCirclesForDirectedCoordinates(tiergarten1, tiergarten2);

            System.out.println(searchGeometry.toString());

            Set<GooglePoi> pois= loader.loadPlaceForMultiCircleSearchGeometry(searchGeometryFactory.createSearchCirclesForDirectedCoordinates(tiergarten1, tiergarten2));

            System.out.println("pios size "+pois.size());

        } catch (ModuleNotWorkingException e) {
            e.printStackTrace();
        }
    }
}