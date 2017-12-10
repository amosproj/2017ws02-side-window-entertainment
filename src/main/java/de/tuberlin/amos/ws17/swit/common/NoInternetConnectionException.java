package de.tuberlin.amos.ws17.swit.common;

public class NoInternetConnectionException extends ModuleNotWorkingException{

    public NoInternetConnectionException() {

    };

    public NoInternetConnectionException(String s) {
        super(s);
    }

    public NoInternetConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoInternetConnectionException(Throwable cause) {
        super(cause);
    }
}