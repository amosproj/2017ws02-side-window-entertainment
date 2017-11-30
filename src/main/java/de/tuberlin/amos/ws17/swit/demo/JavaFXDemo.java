package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.application.view.ApplicationViewImplementation;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFXDemo extends Application {

    public static void main(String[] args) {
        Application.launch(ApplicationViewImplementation.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
