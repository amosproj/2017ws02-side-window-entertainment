package de.tuberlin.amos.ws17.swit.information_source;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;

public class InformationProviderMock implements InformationProvider {

    private static final String info = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod " +
            "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et " +
            "justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor " +
            "sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
            "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et " +
            "ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    @Override
    public String getInfoById(String id) {
        return info;
    }

    @Override
    public String getInfoByName(String name) {
        return info;
    }

    @Override
    public String getNameFromUrl(String url) {
        if (!url.equals("")) {
            String[] temp = url.split("/");
            return temp[temp.length - 1];
        }
        return "";
    }

    @Override
    public PointOfInterest getUrlById(PointOfInterest poi) throws ServiceNotAvailableException {
        poi.setInformationAbstract(info);
        return poi;
    }

    @Override
    public PointOfInterest getInfoByName(PointOfInterest poi) throws ServiceNotAvailableException {
        poi.setInformationAbstract(info);
        return poi;
    }
}
