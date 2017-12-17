package de.tuberlin.amos.ws17.swit.image_analysis;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LandmarkDetectorMock implements LandmarkDetector {

    private List<PointOfInterest> pois = new ArrayList<>();
    private static LandmarkDetector instance;

    private LandmarkDetectorMock() {
        PointOfInterest brandenburgerGate = new PointOfInterest("/m/014kf8", "Brandenburger Gate",
                new GpsPosition(13.378144, 52.516325), ImageUtils.getTestImageFile("brandenburger-tor.jpg"), "");
        PointOfInterest fernsehturm = new PointOfInterest("/m/02g8n0", "Fernsehturm Berlin",
                new GpsPosition(13.424005, 52.521613), ImageUtils.getTestImageFile("fernsehturm-2.jpg"), "");
        PointOfInterest worldClock = new PointOfInterest("/m/02g8n0", "World Clock",
                new GpsPosition(13.413309, 52.521163), ImageUtils.getTestImageFile("fernsehturm.jpg"), "");
        PointOfInterest berlinCathedral = new PointOfInterest("/m/02h7s_", "World Clock",
                new GpsPosition(13.400617, 52.518754), ImageUtils.getTestImageFile("berliner-dom.jpg"), "");
        pois.add(brandenburgerGate);
        pois.add(fernsehturm);
        pois.add(worldClock);
        pois.add(berlinCathedral);
    }

    public static LandmarkDetector getInstance() {
        if (instance == null) {
            instance = new LandmarkDetectorMock();
        }
        return instance;
    }

    @Override
    public List<LandmarkResult> identifyLandmarks(BufferedImage bufferedImage, int maxResults) throws IOException {
        return null;
    }

    @Override
    public List<LandmarkResult> identifyLandmarks(Path imagePath, int maxResults) throws IOException {
        return null;
    }

    @Override
    public List<LandmarkResult> identifyLandmarkUrl(String gcsUrl, int maxResults) throws IOException {
        return null;
    }

    @Override
    public List<PointOfInterest> identifyPOIs(BufferedImage image) {
        int rnd = ThreadLocalRandom.current().nextInt(0, pois.size());
        return Collections.singletonList(pois.get(rnd));
    }
}
