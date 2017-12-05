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
import javafx.geometry.Insets;
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
    private ScrollPane expansionScrollPane;

    private static final String FONTNAME = "Helvetica Neue";
    public static ApplicationViewImplementation app;
    private static ApplicationViewModelImplementation controller;
    private Stage primaryStage;

    public void init() {
        app = this;

        initElements();
        initExpansion();

        controller = new ApplicationViewModelImplementation(this);

        initBindings();
        initCellFactories();
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Side Window Infotainment");
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
        //primaryStage.setMaximized(true);
        Scene scene = new Scene(pnFoundation, 800, 600, Color.WHITE);
        scene.getStylesheets().add("/stylesheets/ApplicationViewStylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initElements() {
        pnFoundation = new BorderPane();
        pnFoundation.setId("pnFoundation");
        listPOIcamera = new ListView<PoiViewModel>();
        listPOIcamera.setId("listPOIcamera");
        pnFoundation.setTop(listPOIcamera);
        listPOImaps = new ListView<PoiViewModel>();
        listPOImaps.setId("listPOImaps");
        pnFoundation.setBottom(listPOImaps);

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
        expansionName.setId("expansionName");
        expansionInformation = new Label();
        expansionInformation.setId("expansionInformation");
        expansionImage = new ImageView();
        expansionImage.setId("expansionImage");
        expansionScrollPane = new ScrollPane();
        expansionScrollPane.setId("expansionScrollPane");
    }

    private void initExpansion() {
        expansionName.setAlignment(Pos.TOP_LEFT);
        expansionInformation.setAlignment(Pos.TOP_CENTER);
        expansionScrollPane.setContent(expansionInformation);
        expansionImage.setPreserveRatio(true);
        expansionImage.setFitHeight(300);
        expansionImage.setFitWidth(150);
        expansionTopPane.setLeft(expansionName);
        expansionTopPane.setRight(expansionButton);
        expansionPane.setTop(expansionTopPane);
        expansionPane.setLeft(expansionImage);
        expansionPane.setCenter(expansionScrollPane);
        pnFoundation.setCenter(expansionPane);
        BorderPane.setAlignment(expansionPane, Pos.CENTER_RIGHT);
        BorderPane.setAlignment(expansionName, Pos.CENTER_LEFT);
        BorderPane.setAlignment(expansionImage, Pos.CENTER_LEFT);
        expansionScrollPane.setFitToWidth(true);
    }

    private void initBindings() {
        expansionButton.visibleProperty().bind(Bindings.equal(controller.getExpandedPOI().nameProperty(), "").not());
        expansionButton.onActionProperty().bindBidirectional(controller.propertyCloseButtonProperty());
        expansionName.textProperty().bindBidirectional(controller.getExpandedPOI().nameProperty());
        expansionImage.imageProperty().bindBidirectional(controller.getExpandedPOI().imageProperty());
        expansionInformation.textProperty().bindBidirectional(controller.getExpandedPOI().informationAbstractProperty());
        listDebugLog.itemsProperty().bindBidirectional(controller.propertyDebugLogProperty());
        listModuleNotWorking.itemsProperty().bindBidirectional(controller.listModuleNotWorkingProperty());
        listPOIcamera.itemsProperty().bindBidirectional(controller.propertyPOIcameraProperty());
        listPOImaps.itemsProperty().bindBidirectional(controller.propertyPOImapsProperty());

    }

    private void initCellFactories() {
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
        listPOIcamera.setCellFactory(callback);
        listPOImaps.setCellFactory(callback);

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
    }

    @Override
    public void stop(){
        controller.run = false;
    }
}