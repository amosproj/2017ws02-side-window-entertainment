package de.tuberlin.amos.ws17.swit.poi.google;

import java.util.List;
import java.util.Set;


import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.GeographicCalculator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class GooglePoiLoaderTest {


	private GooglePoiLoader loader;

	@Before
	public void constrution() throws ModuleNotWorkingException{
		try{
			loader=new GooglePoiLoader(true, 100, 100);
		} catch (ModuleNotWorkingException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void loadOneTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 50);
		assertTrue(pois.size()>1);
		assertTrue(pois.get(0).getPoiTypes().size()>0);
		assertTrue(pois.get(0).getTypes().size()>0);

		System.out.println(pois.get(0).toString());
		System.out.println(pois.get(0).getTypes());
		System.out.println(pois.get(0).getPoiTypes());

	}
	@Test
	public void loadOneTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndType(TestData.TIERGARTEN_POSITION_1, 500, GoogleType.zoo);
		assertTrue(pois.size()>1);

	}
	@Test
	public void loadPoiTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndPoiType(TestData.TIERGARTEN_POSITION_1, 	1000, PoiType.LEISURE);
		assertTrue(pois.size()>0);

	}
	@Test
	public void loadVeryLargeRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 57000);
		assertEquals(pois.size(), 60);
	}
	@Test
	public void loadNoRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 0);
		assertEquals(pois, null);
	}

	@Test
	public void customDirectedSearchTestWithLargeRadius(){
		SearchGeometryFactory searchGeometryFactory=new SearchGeometryFactory(2.8, 600, 3, null, null);
		MultiCircleSearchGeometry searchGeometry=searchGeometryFactory.createSearchCirclesForDirectedCoordinates(TestData.TIERGARTEN_POSITION_1, TestData.TIERGARTEN_POSITION_2);

		System.out.println(searchGeometry.toString());

		Set<GooglePoi> pois= loader.loadPlaceForMultiCircleSearchGeometry(searchGeometryFactory.createSearchCirclesForDirectedCoordinates(TestData.TIERGARTEN_POSITION_1, TestData.TIERGARTEN_POSITION_2));

		System.out.println("pios size "+pois.size());

		//each time 60 pois should be gotten. As its a Set the number can be reduced by overlaps
		assertTrue(pois.size()>100);

	}

	@Test
	public void expandByMetersTest(){
		int distanceInMeters=400;
		GpsPosition pos3= GeographicCalculator.expandByMeters(TestData.TIERGARTEN_POSITION_1, TestData.TIERGARTEN_POSITION_2, distanceInMeters);

		System.out.println("Old1: "+TestData.TIERGARTEN_POSITION_1.toString());
		System.out.println("Old2: "+TestData.TIERGARTEN_POSITION_2.toString());
		System.out.println("Distance in Meters: "+distanceInMeters);
		System.out.println("New: "+ pos3.toString());

		double distance=TestData.TIERGARTEN_POSITION_2.distanceTo(pos3);

		assertTrue(distance>distanceInMeters-2);
		assertTrue(distance<distanceInMeters+2);
	}
}
