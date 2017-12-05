package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.viewmodel.ApplicationViewModelImplementation;
import de.tuberlin.amos.ws17.swit.application.viewmodel.PoiViewModel;
import de.tuberlin.amos.ws17.swit.common.Module;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ApplicationViewImplementation extends Application implements ApplicationView {

    private BorderPane pnFoundation;
    private ListView<PoiViewModel> listPOIcamera;
    private ListView<PoiViewModel> listPOImaps;
    private ListView<String> listDebugLog;
    private ListView<Module> listModuleNotWorking;

    private BorderPane expansionPane;
    private BorderPane expansionTopPane;
    private Button expansionButton;
    private Label expansionName;
    private Label expansionInformation;
    private ImageView expansionImage;

    private static final String FONTNAME = "Helvetica Neue";
    public static ApplicationViewImplementation app;
    private static ApplicationViewModelImplementation controller;
    private Stage primaryStage;

    public void init() {
        app = this;

        pnFoundation = new BorderPane();
        pnFoundation.setId("pnFoundation");
        pnFoundation.setStyle("-fx-background-color: white;");
        listPOIcamera = new ListView<PoiViewModel>();
        listPOIcamera.setId("listPOIcamera");
        listPOImaps = new ListView<PoiViewModel>();
        listPOImaps.setId("listPOImaps");

        listModuleNotWorking = new ListView<>();
        listModuleNotWorking.setId("listModuleNotWorking");
        pnFoundation.setLeft(listModuleNotWorking);

        listDebugLog = new ListView<>();
        listDebugLog.setId("listDebugLog");
        pnFoundation.setRight(listDebugLog);

        expansionPane = new BorderPane();
        expansionPane.setId("expansionPane");
        expansionTopPane = new BorderPane();
        expansionTopPane.setId("expansionTopPane");
        expansionButton = new Button("X");
        expansionButton.setId("expansionButton");
        expansionName = new Label();
        expansionName.setId("expansionPane");
        expansionInformation = new Label();
        expansionInformation.setId("expansionInformation");
        expansionImage = new ImageView();
        expansionImage.setId("expansionImage");

        initExpansion();
    }

    @Override
    public void start(Stage stage) {
        controller = new ApplicationViewModelImplementation(this);

        expansionButton.visibleProperty().bind(Bindings.equal(controller.getExpandedPOI().nameProperty(), "").not());
        expansionButton.onActionProperty().bindBidirectional(controller.propertyCloseButtonProperty());
        expansionName.textProperty().bindBidirectional(controller.getExpandedPOI().nameProperty());
        expansionImage.imageProperty().bindBidirectional(controller.getExpandedPOI().imageProperty());
        expansionInformation.textProperty().bindBidirectional(controller.getExpandedPOI().informationAbstractProperty());

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

        listDebugLog.itemsProperty().bindBidirectional(controller.propertyDebugLogProperty());
        listDebugLog.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Label debugText = new Label(item);
                            setGraphic(debugText);
                            listPOIcamera.refresh();
                        }
                    }
                };
            }
        });

        listModuleNotWorking.itemsProperty().bindBidirectional(controller.listModuleNotWorkingProperty());
        listModuleNotWorking.setMaxWidth(70);
        listModuleNotWorking.setCellFactory(new Callback<ListView<Module>, ListCell<Module>>() {
            @Override
            public ListCell<Module> call(ListView<Module> param) {
                return new ListCell<Module>() {
                    @Override
                    public void updateItem(Module item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            ImageView imageView = new ImageView(SwingFXUtils.toFXImage(item.getModuleImage(), null));
                            imageView.setPreserveRatio(true);
                            imageView.setFitWidth(50);
                            setGraphic(imageView);
                            listPOIcamera.refresh();
                        }
                    }
                };
            }
        });

        listPOIcamera.setPrefHeight(150.0);
        listPOIcamera.itemsProperty().bindBidirectional(controller.propertyPOIcameraProperty());
        listPOIcamera.setOrientation(Orientation.HORIZONTAL);
        listPOIcamera.setCellFactory(callback);
        pnFoundation.setTop(listPOIcamera);

        listPOImaps.setPrefHeight(150.0);
        listPOImaps.itemsProperty().bindBidirectional(controller.propertyPOImapsProperty());
        listPOImaps.setOrientation(Orientation.HORIZONTAL);
        listPOImaps.setCellFactory(callback);
        pnFoundation.setBottom(listPOImaps);

        this.primaryStage = stage;
        primaryStage.setTitle("Side Window Infotainment");
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        //primaryStage.setMaximized(true);
        Scene scene = new Scene(pnFoundation, 800, 600, Color.WHITE);
        scene.getStylesheets().add("/stylesheets/ApplicationViewStylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initExpansion() {
        expansionName.setAlignment(Pos.TOP_LEFT);
        expansionInformation.setAlignment(Pos.TOP_CENTER);
        expansionInformation.setWrapText(true);
        expansionImage.setPreserveRatio(true);
        expansionImage.setFitHeight(200);
        expansionTopPane.setCenter(expansionName);
        expansionTopPane.setRight(expansionButton);
        expansionPane.setTop(expansionTopPane);
        expansionPane.setLeft(expansionImage);
        expansionPane.setCenter(expansionInformation);
        expansionPane.setMaxWidth(500);
        expansionPane.setMaxHeight(400);
        BorderPane.setAlignment(expansionPane, Pos.CENTER_RIGHT);
        BorderPane.setAlignment(expansionImage, Pos.CENTER_LEFT);
        pnFoundation.setCenter(expansionPane);
    }

    @Override
    public void stop(){
        controller.run = false;
    }
}