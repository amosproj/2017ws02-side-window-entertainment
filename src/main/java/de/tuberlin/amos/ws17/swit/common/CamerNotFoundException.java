package de.tuberlin.amos.ws17.swit.common;

public class CamerNotFoundException extends HardwareNotFoundException{

    public CamerNotFoundException() {

    };

    public CamerNotFoundException(String s) {
        super(s);
    }

    public CamerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CamerNotFoundException(Throwable cause) {
        super(cause);
    }
}