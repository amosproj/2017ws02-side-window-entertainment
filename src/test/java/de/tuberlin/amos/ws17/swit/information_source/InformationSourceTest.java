package de.tuberlin.amos.ws17.swit.information_source;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.exceptions.ServiceNotAvailableException;
import de.tuberlin.amos.ws17.swit.poi.MockedPoiService;
import de.tuberlin.amos.ws17.swit.poi.PoiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

@RunWith(JUnit4.class)
public class InformationSourceTest {


    private PoiService poiService = new MockedPoiService();
    private AbstractProvider wiki= new AbstractProvider();

    // check whether Module could successfully fetch the accurate article
    @Test
    public void getWikiArticle(){
        List<PointOfInterest> pois = poiService.loadPlaceForCircle(new GpsPosition(0,0), 0);
        for (PointOfInterest poi:pois ) {
            try {
                poi = wiki.setInfoAndUrl(poi);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println(poi);
        }
    }

    @Test
    public void nullTest(){
        PointOfInterest poi = null;
        PointOfInterest poi2 = new PointOfInterest();
        poi2.setWikiUrl(null);
        poi2.setName(null);
        try {
//            wiki.setInfoAndUrl(poi);
            wiki.setInfoAndUrl(poi2);
        } catch (ServiceNotAvailableException ex) {
            ex.printStackTrace();
        }
    }


}
