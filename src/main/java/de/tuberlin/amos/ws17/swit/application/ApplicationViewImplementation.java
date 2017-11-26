package de.tuberlin.amos.ws17.swit.application;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
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

public class ApplicationViewImplementation extends Application implements ApplicationView, PropertyChangeListener {

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
    private static ApplicationControllerImplementation controller;
    private Stage primaryStage;

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

        Button btnPOIDetection = new Button("Detect");
        btnPOIDetection.setOnAction(event -> {
            controller.analyzeImage();
        });
        pnFoundation.setRight(btnPOIDetection);

        Button btn = new Button("TEST");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.changeTitle();
            }
        });

        pnFoundation.setCenter(btn);
        Button btn2 = new Button("SORT");
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.sortList();
            }
        });
        pnPOIcamera.getChildren().add(btn2);
    }

    @Override
    public void start(Stage stage) {
        controller = new ApplicationControllerImplementation();
        controller.setView(this);
        controller.addPropertyChangeListener(this);
        controller.observableList.addListener(new ListChangeListener<PoiViewModel>() {
            @Override
            public void onChanged(Change<? extends PoiViewModel> c) {
                pnPOImaps.getChildren().remove(0, pnPOImaps.getChildren().size());
                c.next();
                Button btn2 = new Button("SORT");
                btn2.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        controller.sortList();
                    }
                });
                pnPOImaps.getChildren().add(btn2);
                for(PoiViewModel poi: c.getList()) {
                    displayButton(poi);
                }
            }
        });
        Label lbl = new Label();
        pnPOIcamera.getChildren().add(lbl);
        lbl.textProperty().bindBidirectional(controller.getTestSimpleString());

        ListView<PoiViewModel> list = new ListView<PoiViewModel>();
        list.setPrefHeight(200.0);
        pnFoundation.setTop(list);
        list.itemsProperty().bindBidirectional(controller.getTestSimpleListProperty());
        list.setOrientation(Orientation.HORIZONTAL);
        list.setCellFactory(new Callback<ListView<PoiViewModel>, ListCell<PoiViewModel>>() {
            @Override
            public ListCell<PoiViewModel> call(ListView<PoiViewModel> param) {
                return new ListCell<PoiViewModel>() {
                    @Override
                    public void updateItem(PoiViewModel item, boolean empty) {
                        if(item != null) {
                            //Label lblInformation = new Label(item.informationAbstract);
                            ImageView imageView;
                            if (item.image != null) {
                                Image image = SwingFXUtils.toFXImage(item.image, null);
                                imageView = new ImageView(image);
                            } else {
                                File domfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/berliner-dom.jpg").getPath());
                                Image domimage = new Image(domfile.toURI().toString());
                                imageView = new ImageView(domimage);
                            }
                            imageView.setPreserveRatio(true);
                            imageView.setFitHeight(100);
                            Label lblName = new Label(item.name);
                            lblName.setFont(new Font(FONTNAME, 13));
                            BorderPane pane = new BorderPane();
                            pane.setTop(lblName);
                            pane.setCenter(imageView);
                            pane.setOnMouseClicked(event -> controller.onPoiClicked(item));
                            setGraphic(pane);
                        }
                    }
                };
            }
        });

        this.primaryStage = stage;
        primaryStage.setTitle(controller.getTitle());
        //primaryStage.initStyle(StageStyle.TRANSPARENT);
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

    @Override
    public void showPoiInfo(PoiViewModel poi) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(poi.name);
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(poi.image, null));
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        alert.setGraphic(imageView);
        alert.setHeight(200);
        alert.setWidth(200);
        alert.setHeaderText(null);
        alert.setContentText(poi.informationAbstract);
        alert.show();
    }

    public void displayButton(PoiViewModel poi) {
        Button btn = new Button(poi.name);
        pnPOImaps.getChildren().add(btn);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println(evt.getPropertyName());
        primaryStage.setTitle((String)evt.getNewValue());
    }
}
