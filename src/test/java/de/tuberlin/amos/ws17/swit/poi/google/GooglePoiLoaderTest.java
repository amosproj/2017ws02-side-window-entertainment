package de.tuberlin.amos.ws17.swit.poi.google;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PoiVisualiser;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.GeographicCalculator;
import org.junit.Test;

import static org.junit.Assert.*;


public class GooglePoiLoaderTest {

	private double tiergartenLng=13.33470991;
	private double tiergartenLat=52.5083468;
	private double tiergartenLng2=13.33490991;
	private double tiergartenLat2=52.5085468;
	private GpsPosition tiergarten1= new GpsPosition(tiergartenLng, tiergartenLat);
	private GpsPosition tiergarten2= new GpsPosition(tiergartenLng2, tiergartenLat2);

	private final static String GOOGLEPLACESAPIKEY ="AIzaSyAF21uTxKXz139qs8ughPKLuFy91upgHPI";

	private GooglePoiLoader loader=new GooglePoiLoader(true, 100, 100);

	@Test
	public void loadOneTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(tiergarten1, 50);
		assertTrue(pois.size()>1);
		assertTrue(pois.get(0).getPoiTypes().size()>0);
		assertTrue(pois.get(0).getTypes().size()>0);

		System.out.println(pois.get(0).toString());
		System.out.println(pois.get(0).getTypes());
		System.out.println(pois.get(0).getPoiTypes());

	}
	@Test
	public void loadOneTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndType(tiergarten1, 500, GoogleType.zoo);
		assertTrue(pois.size()>1);

	}
	@Test
	public void loadPoiTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndPoiType(tiergarten1, 	1000, PoiType.LEISURE);
		assertTrue(pois.size()>0);

	}
	@Test
	public void loadVeryLargeRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(tiergarten1, 57000);
		assertEquals(pois.size(), 60);
	}
	@Test
	public void loadNoRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(tiergarten1, 0);
		assertEquals(pois, null);
	}
	@Test
	public void noConnectionTest() {
//		List<GooglePoi> pois=loader.loadPlaceForCircleAndPoiType(tiergartenLng, tiergartenLat, 500, PoiType.LEISURE);
//		assertNull(pois);
	}

	@Test
	public void customDirectedSearchTestWithLargeRadius(){
		SearchGeometryFactory searchGeometryFactory=new SearchGeometryFactory(2.8, 600, 3, null, null);
		MultiCircleSearchGeometry searchGeometry=searchGeometryFactory.createSearchCirclesForDirectedCoordinates(tiergarten1, tiergarten2);

		System.out.println(searchGeometry.toString());

		Set<GooglePoi> pois= loader.loadPlaceForMultiCircleSearchGeometry(searchGeometryFactory.createSearchCirclesForDirectedCoordinates(tiergarten1, tiergarten2));

		System.out.println("pios size "+pois.size());

		//each time 60 pois should be gotten. As its a Set the number can be reduced by overlaps
		assertTrue(pois.size()>100);

	}

	@Test
	public void expandByMetersTest(){
		int distanceInMeters=400;
		GpsPosition pos3= GeographicCalculator.expandByMeters(tiergarten1, tiergarten2, distanceInMeters);

		System.out.println("Old1: "+tiergarten1.toString());
		System.out.println("Old2: "+tiergarten2.toString());
		System.out.println("Distance in Meters: "+distanceInMeters);
		System.out.println("New: "+ pos3.toString());

		double distance=tiergarten2.distanceTo(pos3);

		assertTrue(distance>distanceInMeters-2);
		assertTrue(distance<distanceInMeters+2);
	}
}
