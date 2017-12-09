package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.poi.PoisInSightFinder;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoi;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoiLoader;

import java.util.List;
import java.util.Set;

/**
 * A demonstration of the poi package
 * Created by leand on 16.11.2017.
 */
public class MidThermGooglePlacesApiDemo {
    public static void main(String[] args) {

        double tiergartenLng=13.33470991;
        double tiergartenLat=52.5083468;
        double tiergartenLng2=13.33490991;
        double tiergartenLat2=52.5085468;
        GpsPosition previousPostion= new GpsPosition(tiergartenLng, tiergartenLat);
        GpsPosition currentPostion= new GpsPosition(tiergartenLng2, tiergartenLat2);

        GooglePoiLoader loader= null;
        try {
            loader = new GooglePoiLoader(500, 800);
            PoisInSightFinder sightFinder=new PoisInSightFinder(300,200,200);

            // get pois in a circle range
            // you also have the possibility to get them for a certain direction, but I guess thats not necessary for the mid-term release (see: LoadPlacesInDirectionDemo)
            List<GooglePoi> gPois=loader.loadPlaceForCircleAndType(currentPostion,300);

            // dowloading images
            // attenttion this is expensive! 1 request for each photo
            loader.downloadImages(gPois);

            // cast
            List<PointOfInterest> pois=(List) gPois;

            //here the seen pois are calculated by the current position and a previous position
            Set<PointOfInterest> poisInView = sightFinder.getPoisInViewAngle(previousPostion, currentPostion, pois).keySet();

        } catch (ModuleNotWorkingException e) {
            e.printStackTrace();
        }
    }
}