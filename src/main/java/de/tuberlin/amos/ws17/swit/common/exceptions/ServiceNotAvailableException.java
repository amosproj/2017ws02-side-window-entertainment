package de.tuberlin.amos.ws17.swit.common.exceptions;

public class ServiceNotAvailableException extends ModuleNotWorkingException{

    public ServiceNotAvailableException() {

    };

    public ServiceNotAvailableException(String s) {
        super(s);
    }

    public ServiceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceNotAvailableException(Throwable cause) {
        super(cause);
    }
}