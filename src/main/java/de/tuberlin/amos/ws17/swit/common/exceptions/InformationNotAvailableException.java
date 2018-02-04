package de.tuberlin.amos.ws17.swit.common.exceptions;

public class InformationNotAvailableException extends ServiceNotAvailableException {
    public InformationNotAvailableException() {

    };

    public InformationNotAvailableException(String s) {
        super(s);
    }

    public InformationNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public InformationNotAvailableException(Throwable cause) {
        super(cause);
    }
    
}
