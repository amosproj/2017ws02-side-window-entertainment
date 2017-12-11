package de.tuberlin.amos.ws17.swit.common;

public class CameraNotFoundException extends HardwareNotFoundException{

    public CameraNotFoundException() {

    };

    public CameraNotFoundException(String s) {
        super(s);
    }

    public CameraNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CameraNotFoundException(Throwable cause) {
        super(cause);
    }
}