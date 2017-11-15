package de.tuberlin.amos.ws17.swit.demo;

import com.google.api.services.vision.v1.model.BoundingPoly;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkResult;
import de.tuberlin.amos.ws17.swit.information_source.InformationProvider;
import de.tuberlin.amos.ws17.swit.information_source.KnowledgeGraphSearch;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getTestImageFile;

public class LandmarkInfoDemo {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        BufferedImage testImage = getTestImageFile("fernsehturm-2.jpg");
        LandmarkDetector landmarkDetector = CloudVision.getInstance();
        InformationProvider infoProvider = new KnowledgeGraphSearch();

        if (landmarkDetector != null) {
            List<LandmarkResult> results = landmarkDetector.identifyLandmarks(testImage, 3);
            if (!results.isEmpty()) {
                LandmarkResult firstResult = results.get(0);
                String description = infoProvider.getInfoById(firstResult.getId());
//                String description = infoProvider.getInfoByName(firstResult.getName());
                showHighlightedLandmark(firstResult, testImage, description);
                System.out.print(description);
            }
        }
    }

    private static void showHighlightedLandmark(LandmarkResult result, BufferedImage img, String description) {
        // Create a graphics context on the buffered image
        Graphics2D g2d = img.createGraphics();
        JFrame frame = new JFrame();

        BoundingPoly bp = result.getBoundlingPoly();
        // Draw on the buffered image
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(3));
        int npoints = bp.getVertices().size();
        int xpoints[] = new int[npoints];
        int ypoints[] = new int[npoints];
        int xmin = Integer.MAX_VALUE;
        int ymax = 0;
        for (int i = 0; i < npoints; i++) {
            xpoints[i] = bp.getVertices().get(i).getX();
            ypoints[i] = bp.getVertices().get(i).getY();
            if (ypoints[i] > ymax)
                ymax = ypoints[i];
            if (xpoints[i] < xmin) {
                xmin = xpoints[i];
            }
        }

        g2d.drawPolygon(xpoints, ypoints, npoints);
        int xtext = xmin + 10;
        int ytext = ymax - 10;
        g2d.setFont(new Font("Serif", Font.BOLD, 16));
        g2d.drawString(result.getName(), xtext, ytext);


        ImageIcon icon = new ImageIcon(img);
        frame.setLayout(new

                FlowLayout());
        frame.setSize(img.getWidth(), img.getHeight());
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        showInfoBox(description, img, frame, g2d);
    }

    private static void showInfoBox(String description, BufferedImage img, JFrame frame, Graphics2D g2d) {
        String desc = "<html><body style='width: 200px; padding: 5px;'>" + description;
        JLabel textLabel = new JLabel(desc);
        textLabel.setSize(textLabel.getPreferredSize());
        Dimension d = textLabel.getPreferredSize();
        BufferedImage bi = new BufferedImage(
                d.width,
                d.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        g.setColor(new Color(255, 255, 255, 128));
        g.fillRoundRect(
                0,
                0,
                d.width,
                d.height,
                15,
                10);
        g.setColor(Color.black);
        textLabel.paint(g);
        g2d.drawImage(bi, img.getWidth() / 2, 20, frame);
    }

}
