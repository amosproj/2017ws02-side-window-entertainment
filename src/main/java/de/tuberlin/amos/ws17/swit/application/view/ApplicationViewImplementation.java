package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.viewmodel.ApplicationViewModelImplementation;
import de.tuberlin.amos.ws17.swit.application.viewmodel.PoiViewModel;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private TextArea taDebugLog;

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
        pnFoundation.setStyle("-fx-background-color: white;");
        listPOIcamera = new ListView<PoiViewModel>();
        listPOImaps = new ListView<PoiViewModel>();

        taDebugLog = new TextArea();
        taDebugLog.setEditable(false);

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
        Scene scene = new Scene(pnFoundation, 500, 500, Color.TRANSPARENT);
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
        pnFoundation.setCenter(expansionPane);
    }
}