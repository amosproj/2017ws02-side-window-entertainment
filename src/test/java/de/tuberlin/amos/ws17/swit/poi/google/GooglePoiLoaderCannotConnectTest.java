package de.tuberlin.amos.ws17.swit.poi.google;

import de.tuberlin.amos.ws17.swit.common.ModuleNotWorkingException;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by leand on 09.12.2017.
 */
public class GooglePoiLoaderCannotConnectTest {
    @Test
    public void noConnectionTest() {
        try {
            GooglePoiLoader loader = new GooglePoiLoader(true, 100, 100);
            fail("Exception expected. Please check that your internetconnection is broken.");
        }catch (ModuleNotWorkingException e) {
            System.out.println("Expected exception occured, message: " + e.getMessage());
        }

    }

}
