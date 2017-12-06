package de.tuberlin.amos.ws17.swit.landscape_tracking;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class WebcamImplementation {
    protected Webcam webcam;


    protected WebcamImplementation() throws TimeoutException {
        webcam = Webcam.getDefault(2000);
    }

    protected WebcamImplementation(int timeout) throws TimeoutException {
        webcam = Webcam.getDefault(timeout);
    }

    protected WebcamImplementation(String webcamName) {

    }

    protected WebcamImplementation(String webcamName, int timeout) throws TimeoutException {
        List<Webcam> webcamList = Webcam.getWebcams(timeout);
        try{
            for (Webcam tempWebcam: webcamList) {
                if (tempWebcam.toString().startsWith("Webcam Logitech")) {
                    webcam = tempWebcam;
                }
            }
            //webcam = webcamList.parallelStream().filter(w -> w.getName().equals(webcamName)).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException ioobe) {
            System.out.println("Keine Kamera mit den Namen " + webcamName + " gefunden.");
        }

    }

    public Webcam getWebcam() {
        return webcam;
    }
}
