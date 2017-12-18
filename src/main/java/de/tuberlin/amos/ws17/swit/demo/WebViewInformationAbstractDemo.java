package de.tuberlin.amos.ws17.swit.demo;

import javafx.application.Application;
import javafx.stage.Stage;

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