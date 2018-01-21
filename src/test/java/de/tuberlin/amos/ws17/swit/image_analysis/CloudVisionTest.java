package de.tuberlin.amos.ws17.swit.image_analysis;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTracker;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerImplementation;
import org.apache.jena.base.Sys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getRandomTestImage;
import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getTestImageFile;

@RunWith(JUnit4.class)
public class CloudVisionTest {

    private LandmarkDetector appUnderTest;

    @Before
    public void setUp() {
        appUnderTest = CloudVision.getInstance();
    }

    @Test
    public void identifyBrandenburgGate() {
        List<PointOfInterest> pois = appUnderTest.identifyPOIs(getTestImageFile("brandenburger-tor.jpg"));
        assert !pois.isEmpty();
        assert getDescriptions(pois).contains("Brandenburg Gate");
    }

    @Test
    public void identifyBerlinCathedral() {
        List<PointOfInterest> pois = appUnderTest.identifyPOIs(getTestImageFile("berliner-dom.jpg"));
        assert !pois.isEmpty();
        assert getDescriptions(pois).contains("Berlin Cathedral");
    }

    @Test
    public void nullTest() {
        BufferedImage image = null;
        List<PointOfInterest> pois = appUnderTest.identifyPOIs(image);
        assert pois.isEmpty();
    }

    private List<String> getDescriptions(List<PointOfInterest> pois) {
        return pois.stream()
                .map(PointOfInterest::getName)
                .collect(Collectors.toList());
    }

}
