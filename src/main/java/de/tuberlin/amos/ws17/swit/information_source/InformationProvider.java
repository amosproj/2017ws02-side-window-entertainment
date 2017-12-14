package de.tuberlin.amos.ws17.swit.information_source;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.ServiceNotAvailableException;

public interface InformationProvider {

    /**
     * Get additional information about an object
     * @param id Id of object (right now only for Google Knowledge Graph API)
     * @return short description of the object
     */
    String getInfoById(String id);

    /**
     * Get additional information about an object
     * @param name Name of the object
     * @return description of the object
     */
    String getInfoByName(String name);

    /**
     * Get URL link of an object
     * @param url Object
     * @return Url of wikipedia entry (webview relevant)
     */

     String getNameFromUrl (String url);

     PointOfInterest getUrlById(PointOfInterest poi) throws ServiceNotAvailableException;

     PointOfInterest getInfoByName(PointOfInterest poi) throws ServiceNotAvailableException;

}
