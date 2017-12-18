package de.tuberlin.amos.ws17.swit.common.exceptions;

public class CameraNotAvailableException extends HardwareNotAvailableException {

    public CameraNotAvailableException() {

    };

    public CameraNotAvailableException(String s) {
        super(s);
    }

    public CameraNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public CameraNotAvailableException(Throwable cause) {
        super(cause);
    }
}