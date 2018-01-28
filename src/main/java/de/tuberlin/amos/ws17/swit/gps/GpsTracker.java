package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;

public interface GpsTracker extends Module {
	KinematicProperties fillDumpObject(KinematicProperties kinProps) throws ModuleNotWorkingException;
}
