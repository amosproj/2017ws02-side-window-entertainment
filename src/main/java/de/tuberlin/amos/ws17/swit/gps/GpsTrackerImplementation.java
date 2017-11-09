package de.tuberlin.amos.ws17.swit.gps;

class GpsTrackerImplementation implements GpsTracker {
	///If you're 555 then I'm 666 :-)
	public GpsPosition GetGpsPosition()
	{
		return new GpsPosition(555,666);
	}
}
