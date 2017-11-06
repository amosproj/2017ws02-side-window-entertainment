package tub.swit.landscape_tracking;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;

import org.bytedeco.javacpp.opencv_core.IplImage;

import java.awt.image.BufferedImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


class Webcam {


    BufferedImage takePhoto() throws Exception, org.bytedeco.javacv.FrameRecorder.Exception
    {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage grabbedImage;
        {
            grabber.start();
            grabbedImage = converter.convert(grabber.grab());
            grabber.stop();
        }

        return IplImageToBufferedImage(grabbedImage);


    }

    private static BufferedImage IplImageToBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }

}