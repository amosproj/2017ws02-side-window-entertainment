package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.application.viewmodel.*;
import de.tuberlin.amos.ws17.swit.common.DebugTF;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ApplicationViewImplementation extends Application implements ApplicationView {

    private BorderPane                        pnFoundation         = new BorderPane();
    private ListView<PoiViewModel>            listPoiCamera        = new ListView<>();
    private ListView<PoiViewModel>            listPoiMaps          = new ListView<>();
    private ListView<String>                  listDebugLog         = new ListView<>();
    private Label                             textTFDebug          = new Label();
    private ListView<ModuleStatusViewModel>   listModuleStatus     = new ListView<>();
    private ListView<UserExpressionViewModel> listExpressionStatus = new ListView<>();

    // DebugLog Buttons
    private Button toggleUserTrackingLog       = new Button("user_tracking");
    private Button toggleLandscapeTrackingLog  = new Button("landscape_tracking");
    private Button togglePoiLog                = new Button("POI");
    private Button toggleInformationSourceLog  = new Button("information_source");
    private Button toggleImageAnalysisLog      = new Button("image_analysis");
    private Button toggleApplicationViewLog    = new Button("application_view");
    private Button toggleGpsLog                = new Button("GPS");

    private BorderPane  expansionPane        = new BorderPane();
    private BorderPane  expansionTopPane     = new BorderPane();
    private BorderPane  expansionContentPane = new BorderPane();
    private Button      expansionButton      = new Button("X");
    private Label       expansionName        = new Label();
    private Label       expansionInformation = new Label();
    private ImageView   expansionImage       = new ImageView();
    private ScrollPane  expansionScrollPane  = new ScrollPane();
    private HBox        statusPane           = new HBox();
    private HBox        debugPane            = new HBox();
    private VBox        togglePane           = new VBox();
    private StackPane   root                 = new StackPane();
    private MediaPlayer mediaPlayer          = new MediaPlayer(ImageUtils.getTestVideo(ApplicationViewModelImplementation.videoFileName));
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
        stage.setFullScreen(true);
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

        togglePane.setId("togglePane");
        toggleApplicationViewLog.setId("toggleApplicationView");
        toggleImageAnalysisLog.setId("toggleImageAnalysis");
        toggleInformationSourceLog.setId("toggleInformationSource");
        toggleLandscapeTrackingLog.setId("toggleLandscapeTracking");
        togglePoiLog.setId("togglePoi");
        toggleUserTrackingLog.setId("toggleUserTracking");
        toggleGpsLog.setId("toggleGpsLog");

        //togglePane.getStyleClass().add("toggleButton");
        toggleApplicationViewLog.setId("toggleApplicationView");
        toggleApplicationViewLog.getStyleClass().add("toggleButton");
        toggleImageAnalysisLog.setId("toggleImageAnalysis");
        toggleInformationSourceLog.setId("toggleInformationSource");
        toggleLandscapeTrackingLog.setId("toggleLandscapeTracking");
        togglePoiLog.setId("togglePoi");
        toggleUserTrackingLog.setId("toggleUserTracking");
        toggleGpsLog.setId("toggleGpsLog");

        togglePane.getChildren().add(toggleApplicationViewLog);
        togglePane.getChildren().add(toggleGpsLog);
        togglePane.getChildren().add(toggleImageAnalysisLog);
        togglePane.getChildren().add(toggleInformationSourceLog);
        togglePane.getChildren().add(toggleLandscapeTrackingLog);
        togglePane.getChildren().add(togglePoiLog);
        togglePane.getChildren().add(toggleUserTrackingLog);

        debugPane.setId("debugPane");
        debugPane.getChildren().add(togglePane);
        debugPane.getChildren().add(listDebugLog);

        listDebugLog.setId("listDebugLog");
        textTFDebug.setId("textTFDebug");
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
       // root.getChildren().add(listDebugLog);
       // StackPane.setAlignment(listDebugLog, Pos.TOP_RIGHT);
        root.getChildren().add(debugPane);
        StackPane.setAlignment(debugPane, Pos.TOP_RIGHT);
        root.getChildren().add(textTFDebug);
        StackPane.setAlignment(textTFDebug, Pos.BOTTOM_RIGHT);
        Insets insets = new Insets(0, 0, 150, 0);
        StackPane.setMargin(textTFDebug, insets);
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
        BorderPane.setAlignment(expansionPane, Pos.CENTER);
        BorderPane.setAlignment(expansionImage, Pos.CENTER);
        expansionScrollPane.setFitToWidth(true);
    }

    @Override
    public void showDebugLog(boolean show) {
        listDebugLog.setVisible(show);
    }

    @Override
    public void toggleLists() {
        showPoiLists(!listPoiCamera.isVisible());
    }

    private void showPoiLists(boolean show) {
        listPoiCamera.setVisible(show);
        listPoiMaps.setVisible(show);
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
            toggleGpsLog.onActionProperty().bindBidirectional(viewModel.propertyToggleGpsButtonProperty());
            togglePoiLog.onActionProperty().bindBidirectional(viewModel.propertyTogglePoiButtonProperty());
            toggleUserTrackingLog.onActionProperty().bindBidirectional(viewModel.propertyToggleUserTrackingButtonProperty());
            toggleLandscapeTrackingLog.onActionProperty().bindBidirectional(viewModel.propertyToggleLandscapeTrackingButtonProperty());
            toggleImageAnalysisLog.onActionProperty().bindBidirectional(viewModel.propertyToggleImageAnalysisButtonProperty());
            toggleInformationSourceLog.onActionProperty().bindBidirectional(viewModel.propertyToggleInformationSourceButtonProperty());
            toggleApplicationViewLog.onActionProperty().bindBidirectional(viewModel.propertyToggleApplicationViewButtonProperty());
            expansionName.textProperty().bindBidirectional(viewModel.getExpandedPOI().nameProperty());
            expansionImage.imageProperty().bindBidirectional(viewModel.getExpandedPOI().imageProperty());
            expansionInformation.textProperty().bindBidirectional(viewModel.getExpandedPOI().informationAbstractProperty());
            listModuleStatus.itemsProperty().bindBidirectional(viewModel.listModuleStatusProperty());
            listExpressionStatus.itemsProperty().bindBidirectional(viewModel.listExpressionStatusProperty());
            listPoiCamera.itemsProperty().bindBidirectional(viewModel.propertyPoiCameraProperty());
            listPoiMaps.itemsProperty().bindBidirectional(viewModel.propertyPoiMapsProperty());

            listDebugLog.itemsProperty().bindBidirectional(viewModel.propertyDebugLogProperty());
            textTFDebug.textProperty().bindBidirectional(DebugTF.logString);
            if (!AppProperties.getInstance().useDemoVideo) {
                pnFoundation.backgroundProperty().bindBidirectional(viewModel.getBackgroundProperty());
            }

            expansionPane.rotateProperty().bindBidirectional(viewModel.getInfoBoxRotation());
            expansionPane.translateXProperty().bindBidirectional(viewModel.getInfoBoxTranslationX());
            expansionPane.translateYProperty().bindBidirectional(viewModel.getInfoBoxTranslationY());
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

        Callback<ListView<String>, ListCell<String>> logCallBack = param -> new LogTextCell();

        listDebugLog.setCellFactory(logCallBack);

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

        listExpressionStatus.setCellFactory(new Callback<ListView<UserExpressionViewModel>, ListCell<UserExpressionViewModel>>()

        {
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

    private void moveInfoboxByUserPosition() {

    }

    @Override
    public MediaView getMediaView() {
        return mediaView;
    }

    static class LogTextCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(() -> {
                Label debugText = new Label(item);
                setGraphic(debugText);
            });
        }
    }
}
