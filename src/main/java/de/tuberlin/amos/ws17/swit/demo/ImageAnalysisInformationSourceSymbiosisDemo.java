package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.information_source.WikiAPI;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageAnalysisInformationSourceSymbiosisDemo {

    public static void main(String[] args) throws IOException {
        if (args.length != 1){
            System.out.println("Please insert the absolute path to the image you want to have processed as a parameter");
            System.exit(1);
        }

        Path imagePath = Paths.get(args[0]);
        String landmark = CloudVision.getLandmark(imagePath);

        String article = WikiAPI.getArticle(landmark);

        System.out.println(article);
    }
}
