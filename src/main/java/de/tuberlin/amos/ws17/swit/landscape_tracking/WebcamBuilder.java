package de.tuberlin.amos.ws17.swit.landscape_tracking;

import java.awt.*;

public class WebcamBuilder {

    private WebcamImplementation webcamImplementation;


    public WebcamBuilder() {
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
