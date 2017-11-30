package de.tuberlin.amos.ws17.swit.landscape_tracking;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.util.concurrent.TimeoutException;

public class WebcamImplementation {
    protected Webcam webcam;


    protected WebcamImplementation() throws TimeoutException {
        webcam = Webcam.getDefault(2000);
    }

    protected WebcamImplementation(int timeout) throws TimeoutException {
        webcam = Webcam.getDefault(timeout);
    }

    public Webcam getWebcam() {
        return webcam;
    }
}
