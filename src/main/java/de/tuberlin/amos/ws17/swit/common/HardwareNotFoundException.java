package de.tuberlin.amos.ws17.swit.common;

public class HardwareNotFoundException extends ModuleNotWorkingException{

    public HardwareNotFoundException() {

    };

    public HardwareNotFoundException(String s) {
        super(s);
    }

    public HardwareNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HardwareNotFoundException(Throwable cause) {
        super(cause);
    }
}