package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;

public interface GpsTracker extends Module {
	GpsPosition getGpsPosition(); // outdated. Please use fillDumpObject for getting data
	KinematicProperties fillDumpObject(KinematicProperties kinProps) throws ModuleNotWorkingException;
}
