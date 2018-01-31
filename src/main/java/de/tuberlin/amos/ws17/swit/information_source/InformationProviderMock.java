package de.tuberlin.amos.ws17.swit.information_source;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;

public class InformationProviderMock extends AbstractProvider {

    private static final String info = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod " +
            "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et " +
            "justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor " +
            "sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt " +
            "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et " +
            "ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    private static final String wikiUrl = "https://de.wikipedia.org/wiki/Brandenburger_Tor";

    @Override
    public PointOfInterest setInfoAndUrl(PointOfInterest poi) {
        poi.setInformationAbstract(info);
        poi.setWikiUrl(wikiUrl);
        DebugLog.log("Provided mock abstract");
        return poi;
    }
}
