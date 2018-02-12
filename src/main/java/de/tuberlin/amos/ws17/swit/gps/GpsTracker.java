package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.exceptions.InformationNotAvailableException;
import org.joda.time.DateTime;

import java.util.LinkedList;

public interface GpsTracker extends Module {
	KinematicProperties fillDumpObject(KinematicProperties kinProps) throws InformationNotAvailableException;

	// returns list of GpsPoints (with course etc.). 'Count' max. defines length of returned list
	LinkedList<KinematicProperties> getGpsTrack(int count);

	// returns distance travelled since first gps fix
	double getDistanceTravelled();

	// returns time passed since first gps fix
	long getTimePassed();

	// returns current gps position
    GpsPosition getCurrentPosition();
}
