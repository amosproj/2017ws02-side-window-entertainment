package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class DemoVideoGpsTrackerTest {

    private DemoVideoGpsTracker appUnderTest;

    @Before
    public void setUp() {
        try {
            appUnderTest = new DemoVideoGpsTracker();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getGPS() {
        for (int i = 0; i < 20; i++) {
            KinematicProperties kinematicProperties = appUnderTest.fillDumpObject(null);
            System.out.println(kinematicProperties.getLatitude() + ", " + kinematicProperties.getLongitude());
        }
    }

}
