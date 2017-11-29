package de.tuberlin.amos.ws17.swit.common;

import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.google.*;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GpsCoordinateTest {

	private double tiergartenLng=13.33470991;
	private double tiergartenLat=52.5083468;
	private double tiergartenLng2=13.33490991;
	private double tiergartenLat2=52.5085468;
	private GpsPosition tiergarten1= new GpsPosition(tiergartenLng, tiergartenLat);
	private GpsPosition tiergarten2= new GpsPosition(tiergartenLng2, tiergartenLat2);

	@Test
	public void xyTest() {

		int x=tiergarten1.getX();
		int y=tiergarten1.getY();

		GpsPosition posXY=new GpsPosition(x,y);

		double distance=tiergarten1.distanceTo(posXY);

		assertTrue(distance<1);

	}
}
