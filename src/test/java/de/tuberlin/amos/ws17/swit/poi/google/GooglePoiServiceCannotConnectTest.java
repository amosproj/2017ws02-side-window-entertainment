package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * A Test for the {@link GooglePoiService} in case of no connection. The connection of the device must therefore be turned out.
 */
public class GooglePoiServiceCannotConnectTest {
    @Test
    public void noConnectionTest() {
        try {
            GooglePoiService loader = new GooglePoiService(true, 100, 100);
            fail("Exception expected. Please check that your internetconnection is broken.");
        }catch (ModuleNotWorkingException e) {
            System.out.println("Expected exception occured, message: " + e.getMessage());
        }

    }

}
