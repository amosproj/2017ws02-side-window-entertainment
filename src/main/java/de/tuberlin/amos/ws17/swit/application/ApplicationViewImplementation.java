package de.tuberlin.amos.ws17.swit.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApplicationViewImplementation extends Application implements ApplicationView {

    private final BorderPane pnFoundation = new BorderPane();
    private final HBox pnPOIcamera = new HBox();
    private final HBox pnPOImaps = new HBox();
    private final ScrollPane spPOIcamera = new ScrollPane();
    private final ScrollPane spPOImaps = new ScrollPane();

    private final String FONTNAME = "Helvetica Neue";

    private List<Integer> poiID = new ArrayList<Integer>();
    private List<Label> poiName = new ArrayList<Label>();
    private List<ImageView> poiImage = new ArrayList<ImageView>();
    private List<BorderPane> poiPane = new ArrayList<BorderPane>();

    private BorderPane expansionPane = new BorderPane();
    private BorderPane expansionTopPane = new BorderPane();
    private Button expansionButton = new Button("X");
    private Label expansionName = new Label();
    private Label expansionInformation = new Label();
    private ImageView expansionImage = new ImageView();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");

        pnPOIcamera.setPadding(new Insets(5, 5, 5, 5));
        pnPOIcamera.setSpacing(5);
        pnPOIcamera.setStyle("-fx-background-color: transparent;");

        spPOIcamera.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spPOIcamera.setStyle("-fx-background-color: transparent;");
        spPOIcamera.setContent(pnPOIcamera);

        pnPOImaps.setPadding(new Insets(5, 5, 5, 5));
        pnPOImaps.setSpacing(5);
        pnPOImaps.setStyle("-fx-background-color: transparent;");

        spPOImaps.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        spPOIcamera.setStyle("-fx-background-color: transparent;");
        spPOImaps.setContent(pnPOImaps);

        pnFoundation.setStyle("-fx-background-color: transparent;");
        pnFoundation.setTop(spPOIcamera);
        pnFoundation.setBottom(spPOImaps);

        expansionInformation.setAlignment(Pos.TOP_LEFT);
        expansionInformation.setWrapText(true);
        expansionImage.setPreserveRatio(true);
        expansionImage.setFitHeight(200);
        expansionButton.setFont(new Font(FONTNAME, 13));
        expansionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                minimizePOI();
            }
        });
        expansionTopPane.setCenter(expansionName);
        expansionTopPane.setRight(expansionButton);
        expansionPane.setTop(expansionTopPane);
        expansionPane.setLeft(expansionImage);
        expansionPane.setCenter(expansionInformation);

        System.out.println(System.getProperty("user.dir"));
        File file = new File(System.getProperty("user.dir") + "/src/main/resources/test_images/berliner-dom.jpg");
        System.out.println(file.toString());
        System.out.println(file.toURI().toString());
        Image img = new Image(file.toURI().toString());
        System.out.println(img.getHeight());
        System.out.println(img.toString());
        ImageView image = new ImageView(img);
        System.out.println(image.toString());
        displayCameraPOI(5, img, "Berliner Dom");
        expandPOI(5, "Das ist der Berliner Dom, lalalala. Das hier ist ein ganz langer Text um zu testen, " +
                "ob bei einem Label der Tet automatisch auf die nächste Zeile springt. Offensichtlich tut er das nur, wenn man eine Variable dafür setzt. ");

        primaryStage.setScene(new Scene(pnFoundation, 500, 500, Color.TRANSPARENT));
        primaryStage.show();
    }

    public void displayCameraPOI(int id, Image image, String name) {
        poiID.add(id);
        ImageView view = new ImageView(image);
        view.setPreserveRatio(true);
        view.setFitHeight(100);
        poiImage.add(view);
        Label lblName = new Label(name);
        lblName.setFont(new Font(FONTNAME, 13));
        poiName.add(lblName);
        BorderPane pane = new BorderPane();
        pane.setTop(lblName);
        pane.setCenter(view);
        poiPane.add(pane);
        pnPOIcamera.getChildren().add(pane);
    }

    public void displayMapsPOI(int id, String name) {
        poiID.add(id);
        ImageView view = new ImageView();
        poiImage.add(view);
        Label lblName = new Label(name);
        lblName.setFont(new Font(FONTNAME, 13));
        poiName.add(lblName);
        BorderPane pane = new BorderPane();
        pane.setTop(lblName);
        pane.setCenter(view);
        poiPane.add(pane);
        pnPOIcamera.getChildren().add(pane);
    }

    public void removePOI(int id) {
        int index = poiID.indexOf(id);
        pnPOIcamera.getChildren().remove(poiPane.get(index));
        pnPOImaps.getChildren().remove(poiPane.get(index));
        poiID.remove(index);
        poiImage.remove(index);
        poiName.remove(index);
        poiPane.remove(index);
    }

    public void expandPOI(int id, String information) {
        expansionName.setText(poiName.get(poiID.indexOf(id)).getText());
        expansionImage.setImage(poiImage.get(poiID.indexOf(id)).getImage());
        expansionInformation.setText(information);
        pnFoundation.setCenter(expansionPane);
    }

    public boolean minimizePOI() {
        if(pnFoundation.getCenter().equals(expansionPane)) {
            pnFoundation.getChildren().remove(expansionPane);
            return true;
        } else {
            return false;
        }
    }
}
