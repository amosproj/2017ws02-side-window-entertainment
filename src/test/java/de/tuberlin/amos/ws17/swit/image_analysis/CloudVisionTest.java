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
import java.util.List;
import java.util.stream.Collectors;

import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getRandomTestImage;
import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getTestImageFile;

@RunWith(JUnit4.class)
public class CloudVisionTest {
    private static final int MAX_RESULTS = 3;

    private LandmarkDetector appUnderTest;

    @Before
    public void setUp() throws Exception {
        appUnderTest = CloudVision.getInstance();
//        appUnderTest = LandmarkDetectorMock.getInstance();
    }

    @Test
    public void identifyBrandenburgGate() throws Exception {
        List<LandmarkResult> landmarks = appUnderTest.identifyLandmarks(getTestImageFile("brandenburger-tor.jpg"), MAX_RESULTS);
        assert getDescriptions(landmarks).contains("Brandenburg Gate");
    }

    @Test
    public void identifyBerlinCathedral() throws Exception {
        List<LandmarkResult> landmarks = appUnderTest.identifyLandmarks(getTestImageFile("berliner-dom.jpg"), MAX_RESULTS);
        assert getDescriptions(landmarks).contains("Berlin Cathedral");
    }


    @Test
    public void identifyFromWebcamImage() throws Exception {
        List<PointOfInterest> pois = appUnderTest.identifyPOIs(getRandomTestImage());
        for (PointOfInterest p: pois) {
            System.out.println(p);
        }
    }

    private List<String> getDescriptions(List<LandmarkResult> annotations) {
        return annotations.stream()
                .map(LandmarkResult::getName)
                .collect(Collectors.toList());
    }

}
