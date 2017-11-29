package de.tuberlin.amos.ws17.swit.tracking;

import de.tuberlin.amos.ws17.swit.common.UserPosition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UserTrackingPrototyp extends Application {

    private UserTracker userTracker;

    private VBox vBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("UserTracking-Prototyp");

        Button buttonCreateUserTracker = new Button();
        buttonCreateUserTracker.setText("createUserTracker");
        buttonCreateUserTracker.setOnAction(event -> userTracker = new JavoNetUserTracker());

        Button buttonStartTracking = new Button();
        buttonStartTracking.setText("startTracking");
        buttonStartTracking.setOnAction(event -> {
            try {
                userTracker.startTracking();
            } catch (Exception e) {
                printText(e.getMessage());
            }
        });

        Button buttonGetTrackingDetails = new Button();
        buttonGetTrackingDetails.setText("getTrackingDetails");
        buttonGetTrackingDetails.setOnAction(event -> printTrackingDetails());

        Button buttonStopTracking = new Button();
        buttonStopTracking.setText("stopTracking");
        buttonStopTracking.setOnAction(event -> userTracker.stopTracking());

        HBox hBox = new HBox();
        hBox.getChildren().add(buttonCreateUserTracker);
        hBox.getChildren().add(buttonStartTracking);
        hBox.getChildren().add(buttonGetTrackingDetails);
        hBox.getChildren().add(buttonStopTracking);

        vBox = new VBox();
        printText("Prototyp gestartet");

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void printTrackingDetails() {
        boolean isUserTracked = userTracker.getIsUserTracked();
        printText("IsUserTracked: " + isUserTracked);
        if (isUserTracked) {
            UserPosition userPosition = userTracker.getUserPosition();
            printText("IsUserTracked: " + userPosition.toString());
        }
    }

    private void printText(String text) {
        Text newTextLine = new Text(text);
        newTextLine.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        vBox.getChildren().add(newTextLine);
    }
}
