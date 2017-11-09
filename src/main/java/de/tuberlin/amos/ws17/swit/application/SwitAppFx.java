package de.tuberlin.amos.ws17.swit.application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SwitAppFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Bye Bye World!");
        btn.setOnAction(event -> System.out.println("Bye Bye World!"));

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root, 300, 250, Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
