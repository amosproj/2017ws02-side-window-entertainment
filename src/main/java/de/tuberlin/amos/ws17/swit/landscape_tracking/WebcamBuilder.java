package de.tuberlin.amos.ws17.swit.landscape_tracking;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class WebcamBuilder {

    private WebcamImplementation webcamImplementation;

    boolean useTimeout = false;
    int timeout;
    Dimension viewSize;
    String webcamName;

    public WebcamImplementation build() throws TimeoutException {

        if(useTimeout) {
            if(webcamName != null) {
                webcamImplementation = new WebcamImplementation(webcamName, timeout);
            }
            else {
                webcamImplementation = new WebcamImplementation( timeout);
            }
        }
        else {
            if(webcamName != null) {
                webcamImplementation = new WebcamImplementation(webcamName);
            }
            else {
                webcamImplementation = new WebcamImplementation();
            }
        }

        webcamImplementation.webcam.setViewSize(viewSize);
        return webcamImplementation;
    }

    public WebcamBuilder setViewSize(Dimension viewSize) {
        this.viewSize = viewSize;
        return this;
    }

    public WebcamBuilder setWebcamDiscoveryTimeout(int timeout) {
        this.useTimeout = true;
        this.timeout = timeout;
        return this;
    }

    public WebcamBuilder setWebcamName(String webcamName) {
        this.webcamName = webcamName;
        return this;
    }

    public static List<String> getDiscoveredWebcams() {
        return Webcam.getWebcams().parallelStream().map(webcam -> webcam.getName()).collect(Collectors.toList());
    }
}
