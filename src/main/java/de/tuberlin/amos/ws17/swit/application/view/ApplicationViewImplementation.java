package de.tuberlin.amos.ws17.swit.application.view;

import javafx.application.Application;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class ApplicationViewImplementation extends Application implements ApplicationView {

    private BorderPane pnFoundation;
    private HBox pnPOIcamera;
    private HBox pnPOImaps;
    private ScrollPane spPOIcamera;
    private ScrollPane spPOImaps;

    private List<Integer> poiID;
    private List<String> poiInformation;
    private List<Label> poiName;
    private List<ImageView> poiImage;
    private List<BorderPane> poiPane;

    private BorderPane expansionPane;
    private BorderPane expansionTopPane;
    private Button expansionButton;
    private Label expansionName;
    private Label expansionInformation;
    private ImageView expansionImage;

    private static final String FONTNAME = "Helvetica Neue";
    public static ApplicationViewImplementation app;

    //public static void main(String[] args) {launch(args);}

    public void init() {
        app = this;

        pnFoundation = new BorderPane();
        pnPOIcamera = new HBox();
        pnPOImaps = new HBox();
        spPOIcamera = new ScrollPane();
        spPOImaps = new ScrollPane();

        poiID = new ArrayList<Integer>();
        poiInformation = new ArrayList<String>();
        poiName = new ArrayList<Label>();
        poiImage = new ArrayList<ImageView>();
        poiPane = new ArrayList<BorderPane>();

        expansionPane = new BorderPane();
        expansionTopPane = new BorderPane();
        expansionButton = new Button("X");
        expansionName = new Label();
        expansionInformation = new Label();
        expansionImage = new ImageView();

        initView();
        initExpansion();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        //primaryStage.setMaximized(true);

        Scene scene = new Scene(pnFoundation, 500, 500, Color.TRANSPARENT);
        scene.getStylesheets().add("/stylesheets/ApplicationViewStylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initView() {
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
        pnFoundation.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1); -fx-background-radius: 10;");
        pnFoundation.setTop(spPOIcamera);
        pnFoundation.setBottom(spPOImaps);
    }

    private void initExpansion() {
        expansionName.setAlignment(Pos.TOP_LEFT);
        expansionInformation.setAlignment(Pos.TOP_CENTER);
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
    }

    public void displayCameraPOI(int id, String name, Image image, String information) {
        poiID.add(id);
        poiInformation.add(information);
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
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                expandPOI(id);
            }
        });
        poiPane.add(pane);
        pnPOIcamera.getChildren().add(pane);
    }

    public void displayMapsPOI(int id, String name, String information) {
        poiID.add(id);
        poiInformation.add(information);
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
        poiInformation.remove(index);
        poiName.remove(index);
        poiPane.remove(index);
    }

    public void expandPOI(int id) {
        expansionName.setText(poiName.get(poiID.indexOf(id)).getText());
        expansionImage.setImage(poiImage.get(poiID.indexOf(id)).getImage());
        expansionInformation.setText(poiInformation.get(poiID.indexOf(id)));
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
