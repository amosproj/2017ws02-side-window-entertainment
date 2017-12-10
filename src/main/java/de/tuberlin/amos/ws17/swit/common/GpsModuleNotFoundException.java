package de.tuberlin.amos.ws17.swit.common;

public class GpsModuleNotFoundException extends HardwareNotFoundException{

    public GpsModuleNotFoundException() {

    };

    public GpsModuleNotFoundException(String s) {
        super(s);
    }

    public GpsModuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GpsModuleNotFoundException(Throwable cause) {
        super(cause);
    }
}