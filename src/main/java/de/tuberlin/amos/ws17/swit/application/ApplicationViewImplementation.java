package de.tuberlin.amos.ws17.swit.application;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApplicationViewImplementation extends Application implements ApplicationView {

    private BorderPane pnFoundation;
    private ListView<PoiViewModel> listPOIcamera;
    private ListView<PoiViewModel> listPOImaps;

    private BorderPane expansionPane;
    private BorderPane expansionTopPane;
    private Button expansionButton;
    private Label expansionName;
    private Label expansionInformation;
    private ImageView expansionImage;

    private static final String FONTNAME = "Helvetica Neue";
    public static ApplicationViewImplementation app;
    private static ApplicationControllerImplementation controller;
    private Stage primaryStage;

    public void init() {
        app = this;

        pnFoundation = new BorderPane();
        pnFoundation.setStyle("-fx-background-color: white;");
        listPOIcamera = new ListView<PoiViewModel>();
        listPOImaps = new ListView<PoiViewModel>();

        expansionPane = new BorderPane();
        expansionTopPane = new BorderPane();
        expansionButton = new Button("X");
        expansionName = new Label();
        expansionInformation = new Label();
        expansionImage = new ImageView();

        initExpansion();
    }

    @Override
    public void start(Stage stage) {
        controller = new ApplicationControllerImplementation(this);

        expansionName.textProperty().bindBidirectional(controller.expandedPOInameProperty());
        expansionImage.imageProperty().bindBidirectional(controller.expandedPOIimageProperty());
        expansionInformation.textProperty().bindBidirectional(controller.expandedPOIinformationAbstractProperty());

        Callback<ListView<PoiViewModel>, ListCell<PoiViewModel>> callback = new Callback<ListView<PoiViewModel>, ListCell<PoiViewModel>>() {
            @Override
            public ListCell<PoiViewModel> call(ListView<PoiViewModel> param) {
                return new ListCell<PoiViewModel>() {
                    @Override
                    public void updateItem(PoiViewModel item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            ImageView imageView = new ImageView(item.getImage());
                            imageView.setPreserveRatio(true);
                            imageView.setFitHeight(100);
                            Label lblName = new Label(item.getName());
                            lblName.setFont(new Font(FONTNAME, 13));
                            BorderPane pane = new BorderPane();
                            pane.setTop(lblName);
                            pane.setCenter(imageView);
                            pane.setOnMouseClicked(event -> controller.expandPOI(item.getId()));
                            setGraphic(pane);
                            listPOIcamera.refresh();
                        }
                    }
                };
            }
        };

        listPOIcamera.setPrefHeight(150.0);
        listPOIcamera.setStyle("-fx-background-color: white;");
        listPOIcamera.itemsProperty().bindBidirectional(controller.propertyPOIcameraProperty());
        listPOIcamera.setOrientation(Orientation.HORIZONTAL);
        listPOIcamera.setCellFactory(callback);
        pnFoundation.setTop(listPOIcamera);

        listPOImaps.setPrefHeight(150.0);
        listPOImaps.setStyle("-fx-background-color: white;");
        listPOImaps.itemsProperty().bindBidirectional(controller.propertyPOImapsProperty());
        listPOImaps.setOrientation(Orientation.HORIZONTAL);
        listPOImaps.setCellFactory(callback);
        pnFoundation.setBottom(listPOImaps);

        this.primaryStage = stage;
        primaryStage.setTitle("Side Window Infotainment");
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        //primaryStage.setMaximized(true);
        Scene scene = new Scene(pnFoundation, 500, 500, Color.TRANSPARENT);
        scene.getStylesheets().add("/stylesheets/ApplicationViewStylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initExpansion() {
        expansionName.setAlignment(Pos.TOP_LEFT);
        /*expansionName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue == null || newValue.equals("")) {
                    expansionButton.setVisible(false);
                } else {
                    expansionButton.setVisible(true);
                }
            }
        });*/
        expansionInformation.setAlignment(Pos.TOP_CENTER);
        expansionInformation.setWrapText(true);
        expansionImage.setPreserveRatio(true);
        expansionImage.setFitHeight(200);
        expansionButton.setFont(new Font(FONTNAME, 13));
        //expansionButton.setVisible(false);
        expansionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.minimizePOI();
            }
        });
        expansionTopPane.setCenter(expansionName);
        expansionTopPane.setRight(expansionButton);
        expansionPane.setTop(expansionTopPane);
        expansionPane.setLeft(expansionImage);
        expansionPane.setCenter(expansionInformation);
        pnFoundation.setCenter(expansionPane);
    }

    /*public void displayCameraPOI(int id, String name, Image image, String information) {
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

    @Override
    public void showPoiInfo(PoiViewModel poi) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(poi.getName());
        ImageView imageView = new ImageView(poi.getImage());
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        alert.setGraphic(imageView);
        alert.setHeight(200);
        alert.setWidth(200);
        alert.setHeaderText(null);
        alert.setContentText(poi.getInformationAbstract());
        alert.show();
    }

    public void displayButton(PoiViewModel poi) {
        Button btn = new Button(poi.getName());
        pnPOImaps.getChildren().add(btn);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println(evt.getPropertyName());
        primaryStage.setTitle((String)evt.getNewValue());
    }*/
}
