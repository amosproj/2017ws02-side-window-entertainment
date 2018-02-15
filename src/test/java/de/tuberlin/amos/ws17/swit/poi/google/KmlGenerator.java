package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PoiVisualiser;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleViolationException;
import de.tuberlin.amos.ws17.swit.poi.PoisInSightFinder;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Create Kmls for certain requests. Not a asserted test.
 */
public class KmlGenerator {
    private double tiergartenLng=13.33470991;
    private double tiergartenLat=52.5083468;
    private double tiergartenLng2=13.33490991;
    private double tiergartenLat2=52.5085468;
    private GpsPosition tiergarten1= new GpsPosition(tiergartenLng, tiergartenLat);
    private GpsPosition tiergarten2= new GpsPosition(tiergartenLng2, tiergartenLat2);


    private GooglePoiService loader;

    @Before
    void construction() throws ModuleNotWorkingException {
        loader=new GooglePoiService(TestData.apiKey,true, 100, 100, null);
    }

    @Test
    public void loadOneToKml() throws ModuleViolationException {
        List<PointOfInterest> pois2=(List) loader.loadPlaceForCircle(tiergarten1, 500);

        System.out.println(PoiVisualiser.getKmlForPois("loadOneToKml", pois2, null, null, null));
    }

    @Test
    public void inRangeToKml() throws ModuleNotWorkingException, ModuleViolationException {

            GooglePoiService loader = new GooglePoiService(TestData.apiKey,false, 100, 100, null);
            PoisInSightFinder sightFinder = new PoisInSightFinder(500, 300, 500);

            List<GpsPosition> gpsPositions = new ArrayList<>();
            gpsPositions.add(tiergarten1);
            gpsPositions.add(tiergarten2);

            List<PointOfInterest> pois = (List) loader.loadPlaceForCircleAndType(tiergarten2, 500);

            Set<PointOfInterest> poisInSight = sightFinder.getPoisInViewAngle(tiergarten2, tiergarten1, pois).keySet();
            Polygon sight = sightFinder.calculateSight(tiergarten1, tiergarten2);

            String kml = PoiVisualiser.getKmlForPois("inRangeToKml", poisInSight, gpsPositions, tiergarten1, sight);
            System.out.println(kml);

    }
}
