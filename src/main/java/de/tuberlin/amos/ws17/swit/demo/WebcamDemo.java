package de.tuberlin.amos.ws17.swit.demo;

import com.github.sarxos.webcam.WebcamPanel;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTracker;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerImplementation;
import de.tuberlin.amos.ws17.swit.landscape_tracking.WebcamBuilder;
import de.tuberlin.amos.ws17.swit.landscape_tracking.WebcamImplementation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;

public class WebcamDemo {

    public static void main(String[] args) {
        LandscapeTrackerImplementation lst = new LandscapeTrackerImplementation(new WebcamBuilder().setViewSize(new Dimension(640, 480)).build());
        BufferedImage img = null;

        try {
            img = lst.getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel imgLable = new JLabel(new ImageIcon(img));
        /*WebcamPanel panel = new WebcamPanel(lst.webcamImp.getWebcam());
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);

        JFrame window = new JFrame("Test webcam panel");
        window.add(panel);
        window.setResizable(true);
        window.
        window.pack();
        window.setVisible(true);
        */


        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(imgLable);
        frame.pack();
        frame.setVisible(true);

        while(true) {
            try {
                /****************************************/
                img = lst.getImage();
                imgLable.setIcon(new ImageIcon(img));
                /****************************************/

            } catch (IOException e) {
            }
        }



    }
}
