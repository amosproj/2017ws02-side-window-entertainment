package de.tuberlin.amos.ws17.swit.common.exceptions;

public class ModuleViolationException extends Exception {

    public ModuleViolationException() {

    };

    public ModuleViolationException(String s) {
        super(s);
    }

    public ModuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleViolationException(Throwable cause) {
        super(cause);
    }


}