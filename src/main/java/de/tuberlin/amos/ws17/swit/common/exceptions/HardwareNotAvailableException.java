package de.tuberlin.amos.ws17.swit.common.exceptions;

public class HardwareNotAvailableException extends ModuleNotWorkingException{

    public HardwareNotAvailableException() {

    };

    public HardwareNotAvailableException(String s) {
        super(s);
    }

    public HardwareNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public HardwareNotAvailableException(Throwable cause) {
        super(cause);
    }
}