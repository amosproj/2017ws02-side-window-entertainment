package de.tuberlin.amos.ws17.swit.information_source;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

public interface AbstractProvider {

    /**
     * Provides an abstract for a POI
     * @param poi PointOfInterest object, already contains either a name of a poi or its poi
     */
    PointOfInterest provideAbstract(PointOfInterest poi);

}