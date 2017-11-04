package tub.swit.image_analysis;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Paths;
import java.util.List;

@RunWith(JUnit4.class)
public class DetectLandmarkTest {
    private static final int MAX_RESULTS = 3;

    private DetectLandmark appUnderTest;

    @Before
    public void setUp() throws Exception {
        appUnderTest = new DetectLandmark(DetectLandmark.getVisionService());
    }

    @Test public void identifyBrandenburgGate() throws Exception {
        List<EntityAnnotation> landmarks = appUnderTest.identifyLandmark(Paths.get("src/data/brandenburger-tor.jpg"), MAX_RESULTS);

        assert getDescriptions(landmarks).contains("Brandenburg Gate");
    }

    @Test public void identifyBerlinCathedral() throws Exception {
        List<EntityAnnotation> landmarks = appUnderTest.identifyLandmark(Paths.get("src/data/berliner-dom.jpg"), MAX_RESULTS);

        assert getDescriptions(landmarks).contains("Berlin Cathedral");
    }

    private ImmutableSet<String> getDescriptions(List<EntityAnnotation> annotations) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (EntityAnnotation annotation : annotations) {
            System.out.println(annotation.getDescription());
            builder.add(annotation.getDescription());
        }
        return builder.build();
    }
}
