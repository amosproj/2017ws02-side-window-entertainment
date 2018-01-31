package de.tuberlin.amos.ws17.swit.image_analysis;

import com.jayway.jsonpath.PathNotFoundException;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RunWith(JUnit4.class)
public class TFLandmarkClassifierTest {

    private LandmarkDetector appUnderTest;

    @Before
    public void setUp() throws IOException {
        appUnderTest = new TFLandmarkClassifier();
    }

    @Test
    public void identifyFromImagePath() throws IOException {
        List<PointOfInterest> pois = appUnderTest.identifyPOIs(getImagePath("fernsehturm.jpg"));
        assert pois.get(0).getName().equals("Fernsehturm Berlin");

        pois = appUnderTest.identifyPOIs(getImagePath("sieges-saeule.jpg"));
        assert pois.get(0).getName().equals("Siegessäule");

        pois = appUnderTest.identifyPOIs(getImagePath("brandenburger-tor.jpg"));
        assert pois.get(0).getName().equals("Brandenburger Tor");

        pois = appUnderTest.identifyPOIs(getImagePath("fernsehturm-2.jpg"));
        assert pois.get(0).getName().equals("Fernsehturm Berlin");

        pois = appUnderTest.identifyPOIs(getImagePath("berliner-dom.jpg"));
        assert pois.get(0).getName().equals("Berliner Dom");

        pois = appUnderTest.identifyPOIs(getImagePath("holocaust_mahnmal.jpg"));
        assert pois.get(0).getName().equals("Holocaust Mahnmal Berlin");

        pois = appUnderTest.identifyPOIs(getImagePath("alexa.jpg"));
        assert pois.get(0).getName().equals("Alexa Einkaufszentrum");

        pois = appUnderTest.identifyPOIs(getImagePath("cheops_pyramide.jpg"));
        assert pois.isEmpty();

        pois = appUnderTest.identifyPOIs(getImagePath("olympia_stadion.jpg"));
        assert pois.get(0).getName().equals("Olympia Stadio");
    }

    @Test
    public void identifyBufferedImage() {
        BufferedImage image = ImageUtils.getTestImageFile("brandenburger-tor.jpg");
        appUnderTest.identifyPOIs(image);
    }

    private Path getImagePath(String name) {
        return Paths.get("src/main/resources/test_images/" + name);
    }

}
