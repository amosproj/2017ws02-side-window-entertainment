package de.tuberlin.amos.ws17.swit.landscape_tracking;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;

import org.bytedeco.javacpp.opencv_core.IplImage;

import java.awt.image.BufferedImage;




class Webcam {
    OpenCVFrameGrabber grabber;

    boolean running = false;

    Webcam (int deviceNumber) {
        grabber = new OpenCVFrameGrabber(deviceNumber);
    }



    void start() throws Exception {
        grabber.start();
        running = true;
    }

    void stop() throws Exception {
        grabber.stop();
        running = false;
    }

    BufferedImage takePhoto() throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage grabbedImage;
        grabbedImage = converter.convert(grabber.grab());
        return IplImageToBufferedImage(grabbedImage);
    }

    private static BufferedImage IplImageToBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }

}