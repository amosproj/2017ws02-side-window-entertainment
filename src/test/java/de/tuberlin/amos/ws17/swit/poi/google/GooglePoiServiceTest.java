package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleViolationException;
import de.tuberlin.amos.ws17.swit.poi.GeographicCalculator;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
			loader=new GooglePoiService(TestData.apiKey,false, 100, 100, null);
		} catch (ModuleNotWorkingException e){
			e.printStackTrace();
			fail();
		}
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
			loader=new GooglePoiService(TestData.apiKey,false, 100, 100, forbiddenInName);

			List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 500);
			System.out.println("Check forbidden name for "+pois.size()+" Pois.");
			for(PointOfInterest poi:pois){
				assertFalse(poi.getName().contains(forbiddenLetter));
			}
			System.out.println("No place contains test letter: "+forbiddenLetter);

		} catch (ModuleNotWorkingException | ModuleViolationException e){
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * A simple test, loading just one area with no specific demands.
	 */
	@Test
	public void loadOneTest() throws ModuleViolationException {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 50);
		assertTrue(pois.size()>1);

		System.out.println(pois.get(0).toString());
		System.out.println(pois.get(0).getTypes());
		System.out.println(pois.get(0).getPoiTypes());

	}
	/**
	 * Testing behaviour for invalid keys.
	 */
	@Test
	public void invalidKeyTest() throws ModuleViolationException {

		try {
			loader=new GooglePoiService("jo",false, 100, 100, null);
			fail();
		} catch (ModuleNotWorkingException e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 */
	@Test
	public void loadOneTypeTest() throws ModuleViolationException {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndType(TestData.TIERGARTEN_POSITION_1, 500, GoogleType.zoo);
		assertTrue(pois.size()>1);

	}

	/**
	 * Check that retrieval by {@link PoiType} works.
	 */
	@Test
	public void loadPoiTypeTest() throws ModuleViolationException {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndPoiType(TestData.TIERGARTEN_POSITION_1, 	1000, PoiType.LEISURE);
		assertTrue(pois.size()>0);

	}

	/**
	 * Check that for a very large radius the maximum amount ouf pois per request is being received.
	 * @throws ModuleNotWorkingException in case of failure
	 */
	@Test
	public void loadVeryLargeRadiusTest() throws ModuleViolationException {
		List<GooglePoi> pois=loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 57000);
		assertEquals(60, pois.size());
	}

	/**
	 * Check that no radius can lead to an exception or that no poi is returned.
	 */
	@Test
	public void loadNoRadiusTest()  {
		try {
			List<GooglePoi> pois = loader.loadPlaceForCircle(TestData.TIERGARTEN_POSITION_1, 0);
			assertTrue(pois.isEmpty());
		}catch (ModuleViolationException e){

		}
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
