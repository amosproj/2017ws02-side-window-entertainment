package de.tuberlin.amos.ws17.swit.gps;

import net.sf.marineapi.nmea.io.ExceptionListener;

public class OwnExceptionListener implements ExceptionListener {

    // constructor
    public OwnExceptionListener (){

    }

    public void onException(Exception e){
        // do nothing, ignore exceptions
        //System.out.println("Exception: " + e.getMessage());
    }
}
