package de.tuberlin.amos.ws17.swit.common;

public class ModuleNotWorkingException extends Exception {


    public ModuleNotWorkingException() {

    };

    public ModuleNotWorkingException(String s) {
        super(s);
    }

    public ModuleNotWorkingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleNotWorkingException(Throwable cause) {
        super(cause);
    }
}
