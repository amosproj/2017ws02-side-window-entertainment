package de.tuberlin.amos.ws17.swit.information_source;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;

public interface InformationProvider {

    /**
     * Get additional information about an object
     * @param poi Point of interest
     * @return poi with info and wikipedia url
     */
    PointOfInterest setInfoAndUrl(PointOfInterest poi) throws ServiceNotAvailableException;

}
