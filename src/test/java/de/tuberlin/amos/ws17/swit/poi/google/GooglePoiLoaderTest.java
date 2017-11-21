package de.tuberlin.amos.ws17.swit.poi.google;

import java.util.List;

import de.tuberlin.amos.ws17.swit.poi.PoiType;
import org.junit.Test;

import static org.junit.Assert.*;


public class GooglePoiLoaderTest {

	private double tiergartenLng=13.33470991;
	private double tiergartenLat=52.5083468;
	private final static String GOOGLEPLACESAPIKEY ="yourkey";

	private GooglePoiLoader loader=new GooglePoiLoader(GOOGLEPLACESAPIKEY, true);

	@Test
	public void loadOneTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(tiergartenLng, tiergartenLat, 50);
		assertTrue(pois.size()>1);
		assertTrue(pois.get(0).getPoiTypes().size()>0);
		assertTrue(pois.get(0).getTypes().size()>0);

		System.out.println(pois.get(0).toString());
		System.out.println(pois.get(0).getTypes());
		System.out.println(pois.get(0).getPoiTypes());

	}
	@Test
	public void loadOneTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndType(tiergartenLng, tiergartenLat, 500, GoogleType.zoo);
		assertTrue(pois.size()>1);

	}
	@Test
	public void loadPoiTypeTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircleAndPoiType(tiergartenLng, tiergartenLat, 500, PoiType.LEISURE);
		assertTrue(pois.size()>0);

	}
	@Test
	public void loadVeryLargeRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(tiergartenLng, tiergartenLat, 57000);
		assertEquals(pois.size(), 60);
	}
	@Test
	public void loadNoRadiusTest() {
		List<GooglePoi> pois=loader.loadPlaceForCircle(tiergartenLng, tiergartenLat, 0);
		assertEquals(pois, null);
	}
	@Test
	public void noConnectionTest() {
		//TODO
		fail("Not implemented yet.");
	}
}
