package de.tuberlin.amos.ws17.swit.demo;

import com.google.api.services.vision.v1.model.Landmark;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkResult;
import de.tuberlin.amos.ws17.swit.information_source.WikiAPI;
import de.tuberlin.amos.ws17.swit.poi.PoiType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ImageAnalysisInformationSourceSymbiosisDemo {

    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            System.out.println("Please insert the absolute path to the image you want to have processed as a parameter");
            System.exit(1);
        }

        Path imagePath = Paths.get(args[0]);
        List<PointOfInterest> landmarks = CloudVision.getInstance().identifyPOIs(imagePath);
        if (!landmarks.isEmpty()) {
            String article = WikiAPI.getArticle(landmarks.get(0).getName());
            System.out.println(article);
        }


    }
}
