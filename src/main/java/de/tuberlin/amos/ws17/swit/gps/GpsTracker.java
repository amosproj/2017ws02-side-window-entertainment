package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;

public interface GpsTracker {
	GpsPosition getGpsPosition();
	void start();
	void stop();
	void setDumpObject(KinematicProperties kinProps);
}
