package de.tuberlin.amos.ws17.swit.landscape_tracking;

import java.awt.*;
import java.util.concurrent.TimeoutException;

public class WebcamBuilder {

    private WebcamImplementation webcamImplementation;


    public WebcamBuilder(int timeout) throws TimeoutException {
        webcamImplementation = new WebcamImplementation(timeout);
    }

    public WebcamBuilder() throws TimeoutException {
        webcamImplementation = new WebcamImplementation();
    }

    public WebcamImplementation build() {
        return webcamImplementation;
    }

    public WebcamBuilder setViewSize(Dimension viewSize) {
        webcamImplementation.webcam.setViewSize(viewSize);
        return this;
    }
}
