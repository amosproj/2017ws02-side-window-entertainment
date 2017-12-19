package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.poi.GeographicCalculator;
import de.tuberlin.amos.ws17.swit.poi.PoiService;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class MockedPoiServiceTest {


	private PoiService loader=new MockedPoiService();

	@Test
	public void funcionalTest() {
		List<PointOfInterest> pois=loader.loadPlaceForCircle(new GpsPosition(0,0), 0);

		assertTrue(pois.size()>1);
		assertTrue(pois.get(1).getImage()!=null);
		assertTrue(pois.get(0).getImage()==null);

		System.out.println(pois.get(1).getImage().toString());
	}

}
