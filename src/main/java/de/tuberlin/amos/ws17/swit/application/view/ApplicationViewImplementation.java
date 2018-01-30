package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.application.viewmodel.*;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.jena.base.Sys;

public class ApplicationViewImplementation extends Application implements ApplicationView {

    private BorderPane                        pnFoundation         = new BorderPane();
    private ListView<PoiViewModel>            listPoiCamera        = new ListView<>();
    private ListView<PoiViewModel>            listPoiMaps          = new ListView<>();
    private ListView<String>                  listDebugLog         = new ListView<>();
    private ListView<ModuleStatusViewModel>   listModuleStatus     = new ListView<>();
    private ListView<UserExpressionViewModel> listExpressionStatus = new ListView<>();

    private BorderPane  expansionPane        = new BorderPane();
    private BorderPane  expansionTopPane     = new BorderPane();
    private BorderPane  expansionContentPane = new BorderPane();
    private Button      expansionButton      = new Button("X");
    private Label       expansionName        = new Label();
    private Label       expansionInformation = new Label();
    private ImageView   expansionImage       = new ImageView();
    private ScrollPane  expansionScrollPane  = new ScrollPane();
    private HBox        statusPane           = new HBox();
    private StackPane   root                 = new StackPane();
    private MediaPlayer mediaPlayer          = new MediaPlayer(ImageUtils.getTestVideo("Berlin.mp4"));
    private MediaView mediaView;

    private static final String FONTNAME = "Helvetica Neue";

    private static ApplicationViewModel viewModel;

    public void init() {
        initElements();
        initExpansion();
        viewModel = new ApplicationViewModelImplementation(this);

        initBindings();
        initCellFactories();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Side Window Infotainment");

        Scene scene = new Scene(root, 800, 600, Color.WHITE);
        scene.setOnKeyPressed(event -> viewModel.onKeyPressed(event.getCode()));
        scene.getStylesheets().add("/stylesheets/TransparentApplicationViewStylesheet.css");
        stage.setScene(scene);
        stage.show();
    }

    private void initElements() {
        pnFoundation.setId("pnFoundation");
        pnFoundation.setTop(listPoiCamera);
        pnFoundation.setBottom(listPoiMaps);
        pnFoundation.setLeft(statusPane);

        listPoiCamera.setId("listPoiCamera");
        listPoiMaps.setId("listPoiMaps");

        statusPane.setId("statusPane");
        statusPane.getChildren().add(listModuleStatus);
        statusPane.getChildren().add(listExpressionStatus);

        listModuleStatus.setId("listModuleStatus");

        listExpressionStatus.setId("listExpressionStatus");

        listDebugLog.setId("listDebugLog");
        expansionPane.setId("expansionPane");
        expansionPane.setVisible(false);
        expansionTopPane.setId("expansionTopPane");
        expansionButton.setId("expansionButton");
        expansionName.setId("expansionName");
        expansionContentPane.setId("expansionContentPane");
        expansionInformation.setId("expansionInformation");
        expansionImage.setId("expansionImage");
        expansionScrollPane.setId("expansionScrollPane");

        mediaView = new MediaView(mediaPlayer);
        mediaView.setVisible(false);
        final DoubleProperty width = mediaView.fitWidthProperty();
        final DoubleProperty height = mediaView.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        root.getChildren().add(mediaView);
        root.getChildren().add(pnFoundation);
        root.getChildren().add(listDebugLog);
        StackPane.setAlignment(listDebugLog, Pos.TOP_RIGHT);
    }

    private void initExpansion() {
        expansionName.setAlignment(Pos.TOP_CENTER);
        expansionInformation.setAlignment(Pos.TOP_CENTER);
        expansionContentPane.setTop(expansionImage);
        expansionContentPane.setBottom(expansionInformation);
        expansionScrollPane.setContent(expansionContentPane);
        expansionImage.setPreserveRatio(true);
        expansionImage.setFitHeight(150);
        expansionImage.minHeight(0);
        expansionImage.minWidth(0);

        expansionTopPane.setLeft(expansionName);
        expansionTopPane.setRight(expansionButton);
        expansionPane.setTop(expansionTopPane);
        expansionPane.setCenter(expansionImage);
        expansionPane.setBottom(expansionScrollPane);
        pnFoundation.setCenter(expansionPane);
        BorderPane.setAlignment(expansionPane, Pos.CENTER_RIGHT);
        BorderPane.setAlignment(expansionImage, Pos.CENTER);
        expansionScrollPane.setFitToWidth(true);
    }

    @Override
    public void showDebugLog(boolean show) {
        listDebugLog.setVisible(show);
    }

    @Override
    public void toggleDebugLog() {
        listDebugLog.setVisible(!listDebugLog.isVisible());
    }

    private void initBindings() {
        ImageView cameraImage = new ImageView();
        cameraImage.imageProperty().bindBidirectional(viewModel.propertyCameraImageProperty());

        try {
            expansionButton.visibleProperty().bind(Bindings.equal(viewModel.getExpandedPOI().nameProperty(), "").not());
            expansionButton.onActionProperty().bindBidirectional(viewModel.propertyCloseButtonProperty());
            expansionName.textProperty().bindBidirectional(viewModel.getExpandedPOI().nameProperty());
            expansionImage.imageProperty().bindBidirectional(viewModel.getExpandedPOI().imageProperty());
            expansionInformation.textProperty().bindBidirectional(viewModel.getExpandedPOI().informationAbstractProperty());
            listModuleStatus.itemsProperty().bindBidirectional(viewModel.listModuleStatusProperty());
            listExpressionStatus.itemsProperty().bindBidirectional(viewModel.listExpressionStatusProperty());
            listPoiCamera.itemsProperty().bindBidirectional(viewModel.propertyPoiCameraProperty());
            listPoiMaps.itemsProperty().bindBidirectional(viewModel.propertyPoiMapsProperty());

            listDebugLog.itemsProperty().bindBidirectional(viewModel.propertyDebugLogProperty());
            if (!AppProperties.getInstance().useDemoVideo) {
                pnFoundation.backgroundProperty().bindBidirectional(viewModel.getBackgroundProperty());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initCellFactories() {
        Callback<ListView<PoiViewModel>, ListCell<PoiViewModel>> callback = new Callback<ListView<PoiViewModel>, ListCell<PoiViewModel>>() {
            @Override
            public ListCell<PoiViewModel> call(ListView<PoiViewModel> param) {
                return new ListCell<PoiViewModel>() {
                    @Override
                    public void updateItem(PoiViewModel item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
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
                            pane.setOnMouseClicked(event -> viewModel.expandPoi(item.getId()));
                            setGraphic(pane);
                            listPoiCamera.refresh();
                            listPoiMaps.refresh();
                        }
                    }
                };
            }
        };
        listPoiCamera.setCellFactory(callback);
        listPoiMaps.setCellFactory(callback);

        listDebugLog.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        Platform.runLater(() -> {
                            if (empty || item == null) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                Label debugText = new Label(item);
                                setGraphic(debugText);
                                listDebugLog.refresh();
                            }
                        });

                    }
                };
            }
        });

        listModuleStatus.setCellFactory(new Callback<ListView<ModuleStatusViewModel>, ListCell<ModuleStatusViewModel>>() {
            @Override
            public ListCell<ModuleStatusViewModel> call(ListView<ModuleStatusViewModel> param) {
                return new ListCell<ModuleStatusViewModel>() {
                    @Override
                    public void updateItem(ModuleStatusViewModel item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            StackPane pane = new StackPane();
                            ImageView image = new ImageView(item.getErrorType().getImage());
                            image.setPreserveRatio(true);
                            image.setFitWidth(50);
                            image.setFitHeight(50);
                            Label lbl = new Label();
                            item.workingProperty().addListener((observable, oldValue, newValue) -> statusIcon(lbl, newValue));
                            statusIcon(lbl, item.isWorking());
                            pane.getChildren().add(image);
                            pane.getChildren().add(lbl);
                            StackPane.setAlignment(lbl, Pos.BOTTOM_RIGHT);
                            StackPane.setAlignment(image, Pos.CENTER_LEFT);
                            setGraphic(pane);
                            listModuleStatus.refresh();
                        }
                    }
                };
            }
        });

        listExpressionStatus.setCellFactory(new Callback<ListView<UserExpressionViewModel>, ListCell<UserExpressionViewModel>>() {
            @Override
            public ListCell<UserExpressionViewModel> call(ListView<UserExpressionViewModel> param) {
                return new ListCell<UserExpressionViewModel>() {
                    @Override
                    public void updateItem(UserExpressionViewModel item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            StackPane pane = new StackPane();
                            ImageView image = new ImageView(item.getType().getImage());
                            image.setPreserveRatio(true);
                            image.setFitWidth(50);
                            image.setFitHeight(50);
                            Label lbl = new Label();
                            item.activeProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> statusIcon(lbl, newValue)));
                            statusIcon(lbl, item.isActive());
                            pane.getChildren().add(image);
                            pane.getChildren().add(lbl);
                            StackPane.setAlignment(lbl, Pos.BOTTOM_RIGHT);
                            StackPane.setAlignment(image, Pos.CENTER_RIGHT);
                            setGraphic(pane);
                            listExpressionStatus.refresh();
                        }
                    }
                };
            }
        });
    }

    private void statusIcon(Label lbl, boolean status) {
        if (status) {
            String text = new String(Character.toChars(10003));
            lbl.setText(text);
            lbl.setStyle("-fx-text-fill: green; -fx-font-size: 20px; -fx-font-weight: bold; " +
                    "-fx-background-color: transparent; -fx-effect: dropshadow( gaussian , white , 3, 1.0 , 0 , 0 );");
        } else {
            String text = new String(Character.toChars(10005));
            lbl.setText(text);
            lbl.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-font-weight: bold; " +
                    "-fx-background-color: transparent; -fx-effect: dropshadow( gaussian , white , 3, 1.0 , 0 , 0 );");
        }
    }

    public void showExpandedPoi(boolean show) {
        expansionPane.setVisible(show);
    }

    @Override
    public void stop() {
        viewModel.setRunning(false);
        for (Module m : viewModel.getModuleList()) {
            m.stopModule();
        }
        System.exit(0);
    }


    @Override
    public MediaView getMediaView() {
        return mediaView;
    }
}