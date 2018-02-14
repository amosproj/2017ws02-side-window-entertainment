package de.tuberlin.amos.ws17.swit.poi.google;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.GeographicCalculator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * A test class for the google poi service
 */
public class GooglePoiServiceTest {


	private GooglePoiService loader;

	/**
	 * Initialisation of each test
	 */
	@Before
	public void construction() {
		try{
			loader=new GooglePoiService(false, 100, 100, null);
		} catch (ModuleNotWorkingException e){
			e.printStackTrace();
			fail();
		}
		loadOneTypeTest();
	}

	/**
	 * Testing the functionality of the fourth constructor parameter (forbidden Strings) of the {@link GooglePoiService}.
	 * Therefore it does not use the {@link GooglePoiService} object that is being initialized in the construction (@before) method
	 */
	@Test
	public void forbiddenInNameTest() {
		String forbiddenLetter="a";
		List<String> forbiddenInName=new ArrayList<>();
		forbiddenInName.add(forbiddenLetter);
		try{
			loader=new GooglePoiService(false, 100, 100, forbiddenInName);

			List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 500);
			System.out.println("Check forbidden name for "+pois.size()+" Pois.");
			for(PointOfInterest poi:pois){
				assertFalse(poi.getName().contains(forbiddenLetter));
			}
			System.out.println("No place contains test letter: "+forbiddenLetter);

		} catch (ModuleNotWorkingException e){
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * A simple test, loading just one area with no specific demands.
	 */
	@Test
	public void loadOneTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 50);
		assertTrue(pois.size()>1);

		System.out.println(pois.get(0).toString());
		System.out.println(pois.get(0).getTypes());
		System.out.println(pois.get(0).getPoiTypes());

	}

	/**
	 *
	 */
	@Test
	public void loadOneTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndType(TestData.TIERGARTEN_POSITION_1, 500, GoogleType.zoo);
		assertTrue(pois.size()>1);

	}

	/**
	 * Check that retrieval by {@link PoiType} works.
	 */
	@Test
	public void loadPoiTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndPoiType(TestData.TIERGARTEN_POSITION_1, 	1000, PoiType.LEISURE);
		assertTrue(pois.size()>0);

	}

	/**
	 * Check that for a very large radius the maximum amount ouf pois per request is being received.
	 * @throws ModuleNotWorkingException in case of failure
	 */
	@Test
	public void loadVeryLargeRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 57000);
		assertEquals(60, pois.size());
	}

	/**
	 * Check that no radius is possible but also that no poi is returned.
	 */
	@Test
	public void loadNoRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 0);
		assertTrue(pois.isEmpty());
	}

	/**
	 * Check the that the methode expandByMeters by the {@link GeographicCalculator} works correctly.
	 */
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
