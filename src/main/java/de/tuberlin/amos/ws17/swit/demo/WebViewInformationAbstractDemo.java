package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.common.ServiceNotAvailableException;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkResult;
import de.tuberlin.amos.ws17.swit.information_source.InformationProvider;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import de.tuberlin.amos.ws17.swit.information_source.KnowledgeGraphSearch;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getTestImageFile;

public class WebViewInformationAbstractDemo extends Application {

    @Override
    public void start(final Stage stage) {

        /*BufferedImage testImage = getTestImageFile("fernsehturm-2.jpg");
        LandmarkDetector landmarkDetector = CloudVision.getInstance();
        final InformationProvider entity;
        try {
            entity = KnowledgeGraphSearch.getInstance();
        } catch (ServiceNotAvailableException e) {
            e.printStackTrace();
        }


        Button buttonURL = new Button("Analyze Image");

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        buttonURL.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    if (landmarkDetector != null) {
                        List<LandmarkResult> results = landmarkDetector.identifyLandmarks(testImage, 3);
                        if (!results.isEmpty()) {
                            LandmarkResult firstResult = results.get(0);
                            String description = entity.getInfoById(firstResult.getId());
                            webEngine.load(entity.getUrlById(results.get(0).getId()));
                            System.out.print(description);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        VBox root = new VBox();
        root.setPadding(new Insets(5));
        root.setSpacing(5);
        root.getChildren().addAll(buttonURL, browser);

        Scene scene = new Scene(root);

        stage.setTitle("Webview");
        stage.setScene(scene);
        stage.setWidth(450);
        stage.setHeight(300);

        stage.show();*/
    }

    public static void main(String[] args) {
        launch(args);
    }

}