package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.application.ApplicationViewImplementation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class JavaFXDemo extends Application {

    public static void main(String[] args) {
        Application.launch(ApplicationViewImplementation.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
