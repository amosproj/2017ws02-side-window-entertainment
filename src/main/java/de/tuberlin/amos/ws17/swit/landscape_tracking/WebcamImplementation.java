package de.tuberlin.amos.ws17.swit.landscape_tracking;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;

public class WebcamImplementation {
    protected Webcam webcam;


    protected WebcamImplementation() {
        webcam = Webcam.getDefault();
    }

    public Webcam getWebcam() {
        return webcam;
    }
}
