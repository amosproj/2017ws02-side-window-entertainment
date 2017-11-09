package de.tuberlin.amos.ws17.swit.image_analysis;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class CloudVisionClientTest {
    private static final int MAX_RESULTS = 3;

    private CloudVisionClient appUnderTest;

    @Before
    public void setUp() throws Exception {
        appUnderTest = new CloudVisionClient(CloudVisionClient.getVisionService());
    }

    @Test public void identifyBrandenburgGate() throws Exception {
        List<LandmarkResult> landmarks = appUnderTest.identifyLandmark(Paths.get("src/data/brandenburger-tor.jpg"), MAX_RESULTS);

        assert getDescriptions(landmarks).contains("Brandenburg Gate");
    }

    @Test public void identifyBerlinCathedral() throws Exception {
        List<LandmarkResult> landmarks = appUnderTest.identifyLandmark(Paths.get("src/data/berliner-dom.jpg"), MAX_RESULTS);

        assert getDescriptions(landmarks).contains("Berlin Cathedral");
    }

    @Test public void showImage() {
        Path path = Paths.get("src/data/brandenburger-tor.jpg");
    }

    private List<String> getDescriptions(List<LandmarkResult> annotations) {
        return annotations.stream()
                .map(LandmarkResult::getName)
                .collect(Collectors.toList());
    }
}
