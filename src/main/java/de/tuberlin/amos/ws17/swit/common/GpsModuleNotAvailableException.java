package de.tuberlin.amos.ws17.swit.common;

public class GpsModuleNotAvailableException extends HardwareNotAvailableException {

    public GpsModuleNotAvailableException() {

    };

    public GpsModuleNotAvailableException(String s) {
        super(s);
    }

    public GpsModuleNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public GpsModuleNotAvailableException(Throwable cause) {
        super(cause);
    }
}