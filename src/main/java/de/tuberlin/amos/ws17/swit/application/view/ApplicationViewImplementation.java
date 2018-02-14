package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.application.viewmodel.*;
import de.tuberlin.amos.ws17.swit.common.AnimationUtils;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.DebugTF;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class ApplicationViewImplementation extends Application implements ApplicationView {
    //##### layer container #####
    private StackPane stackPaneRoot = null;
    //##########

    //##### media layer #####
    private MediaView   mediaLayer  = null;
    private MediaPlayer mediaPlayer = null;
    //##########

    //##### application layer #####
    private GridPane    applicationLayer = null;
    private BorderPane  infoBoxLayer     = null;
    //### infobox
    private InfoBoxView infoBoxView      = null;

    //### Poi Lists
    private ListView<PoiViewModel> listPoiCamera = null;
    private ListView<PoiViewModel> listPoiMaps   = null;

    //### Status: expressions and module status
    private HBox                              statusPane           = null;
    private ListView<ModuleStatusViewModel>   listModuleStatus     = null;
    private ListView<UserExpressionViewModel> listExpressionStatus = null;
    //##########

    //##### debug layer #####
    private GridPane debugLayer   = null;
    //### DebugLog
    private DebugLogView debugLogView = null;

    //### TensorFlow Debug
    private Label            labelTensorFlowDebug = null;
    //##########

    //private static final String FONTNAME = "Helvetica Neue";
    private static double fontSizeTitle = 12;
    private static double fontSizeItem  = 12;
    private static double fontSizeText  = 12;

    private static double itemWidth = 0;

    private static Font fontTitle = null;
    private static Font fontItem  = null;
    private static Font fontText  = null;

    private static ApplicationViewModel viewModel;

    /**
     * initializes the UI
     */
    public void init() {

        initFonts();
        initRootStackPane();

        viewModel = new ApplicationViewModelImplementation(this);

        initBindings();
        initCellFactories();
    }

    /**
     * initalizes font sizes and values to scale items to the monitors resolution
     */
    private void initFonts() {
        Screen screen = Screen.getPrimary();
        Rectangle2D screenVisualBounds = screen.getVisualBounds();

        itemWidth = screenVisualBounds.getWidth() * 0.18;

        fontSizeTitle = screenVisualBounds.getHeight() * 0.03;
        fontSizeItem = screenVisualBounds.getHeight() * 0.02;
        fontSizeText = screenVisualBounds.getHeight() * 0.015;

        fontTitle = new Font(fontSizeTitle);
        fontItem = new Font(fontSizeItem);
        fontText = new Font(fontSizeText);
    }

    /**
     * initializes base root element which is a stack pane that layers media, application and debug over each other
     */
    private void initRootStackPane() {
        stackPaneRoot = new StackPane();

        initMediaLayer();
        initApplicationLayer();
        initDebugLayer();

        if (mediaLayer != null) {
            stackPaneRoot.getChildren().add(mediaLayer);
        }

        stackPaneRoot.getChildren().add(applicationLayer);
        stackPaneRoot.getChildren().add(debugLayer);
        stackPaneRoot.getChildren().add(infoBoxLayer);
    }

    /**
     * initalizes media layer (first (lowest) layer)
     */
    private void initMediaLayer() {
        try {
            mediaPlayer = new MediaPlayer(ImageUtils.getTestVideo(ApplicationViewModelImplementation.videoFileName));
            mediaLayer = new MediaView(mediaPlayer);
            mediaLayer.setVisible(false);
            final DoubleProperty width = mediaLayer.fitWidthProperty();
            final DoubleProperty height = mediaLayer.fitHeightProperty();

            width.bind(Bindings.selectDouble(mediaLayer.sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(mediaLayer.sceneProperty(), "height"));
            mediaLayer.setPreserveRatio(true);
        } catch (Exception e) {
            DebugLog.log(DebugLog.SOURCE_VIEW, "MediaPlayer kann nicht initialisiert werden");
        }
    }

    /**
     * initalizes application layer (second layer)
     */
    private void initApplicationLayer() {
        applicationLayer = new GridPane();
        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(0).setPercentHeight(12.0);
        applicationLayer.getRowConstraints().get(0).setValignment(VPos.CENTER);

        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(1).setPercentHeight(16.0);
        applicationLayer.getRowConstraints().get(1).setValignment(VPos.CENTER);

        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(2).setPercentHeight(44.0);
        applicationLayer.getRowConstraints().get(2).setValignment(VPos.CENTER);

        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(3).setPercentHeight(16.0);
        applicationLayer.getRowConstraints().get(3).setValignment(VPos.CENTER);

        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(4).setPercentHeight(12.0);
        applicationLayer.getRowConstraints().get(4).setValignment(VPos.CENTER);

        int numColumns = 5;
        for (int i = 0; i < numColumns; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / numColumns);
            cc.setHalignment(HPos.CENTER);
            applicationLayer.getColumnConstraints().add(cc);
        }

        initListPoiCamera();
        initListPoiMaps();
        initStatusPane();
        initInfoboxPane();

        applicationLayer.add(listPoiCamera, 0, 0, numColumns, 1);
        applicationLayer.add(listPoiMaps, 0, 4, numColumns, 1);
        applicationLayer.add(statusPane, 0, 2);
    }

    /**
     * initalizes the list at the top for camera POIs
     */
    private void initListPoiCamera() {
        listPoiCamera = new ListView<>();
        listPoiCamera.setId("listPoiCamera");
    }

    /**
     * initalizes the list at the bottom for maps POIs
     */
    private void initListPoiMaps() {
        listPoiMaps = new ListView<>();
        listPoiMaps.setId("listPoiMaps");
    }

    /**
     * initalizes the status pane on the left side which shows the status of expressions and of the modules
     */
    private void initStatusPane() {
        listModuleStatus = new ListView<>();
        listModuleStatus.setId("listModuleStatus");

        listExpressionStatus = new ListView<>();
        listExpressionStatus.setId("listExpressionStatus");

        statusPane = new HBox();
        statusPane.setId("statusPane");
        statusPane.getChildren().add(listModuleStatus);
        statusPane.getChildren().add(listExpressionStatus);
    }

    /**
     * initalizes the infobox which shows the information to a POI in the middle of the screen (infobox is in the highest layer (fourth))
     */
    private void initInfoboxPane() {
        infoBoxView = new InfoBoxView();
        infoBoxLayer = new BorderPane();
        infoBoxLayer.setCenter(infoBoxView);
        infoBoxLayer.setPickOnBounds(false);
    }

    /**
     * initalizes the debug layer with the debug window on the right side
     */
    private void initDebugLayer() {
        debugLayer = new GridPane();
        debugLayer.getRowConstraints().addAll(applicationLayer.getRowConstraints());
        debugLayer.getColumnConstraints().addAll(applicationLayer.getColumnConstraints());
        debugLayer.setPickOnBounds(false);

        initDebugTensorFlow();
        debugLogView = new DebugLogView();
        debugLayer.add(debugLogView, 3, 1, 2, 2);
        debugLayer.add(labelTensorFlowDebug, 4, 3);
    }

    /**
     * initalizes the tensor flow box in the right bottom corner
     */
    private void initDebugTensorFlow() {
        labelTensorFlowDebug = new Label();
        labelTensorFlowDebug.setId("textTFDebug");
        labelTensorFlowDebug.setVisible(false);
    }

    /**
     * starting method forthe UI
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Side Window Infotainment");

        Scene scene = new Scene(stackPaneRoot, 1920, 1080, Color.WHITE);
        scene.setOnKeyPressed(event -> viewModel.onKeyPressed(event.getCode())) ;
        scene.getStylesheets().add("/stylesheets/TransparentApplicationViewStylesheet.css");
        stage.setScene(scene);
        initFullscreenMode(stage);
        stage.show();

        // need to be set here, because scrollbar can only be found after being added to stage
        infoBoxView.setOnScrollListener(event -> {
            viewModel.onInfoTextScrolled();
            infoBoxView.getIndicator().setVisible(false);
        });
    }

    /**
     * if fullscreen or maximized properties are set, they are being applied here
     */
    private void initFullscreenMode(Stage stage) {
        if (AppProperties.getInstance().useFullscreen) {
            stage.setMaximized(true);
        } else if (AppProperties.getInstance().useFullscreenWithoutWindowChrome) {
            stage.setFullScreen(true);
        }
    }

    /**
     * switches the tensorflow window visibility on/off
     */
    @Override
    public void toggleTensorFlowDebugWindow() {
        labelTensorFlowDebug.setVisible(!labelTensorFlowDebug.isVisible());
    }

    /**
     * sets the visibility of the debuglog
     */
    @Override
    public void showDebugLog(boolean show) {
        debugLogView.setVisible(show);
    }

    /**
     * toggles the visibility of the lists at the top and at the bottom
     */
    @Override
    public void toggleLists() {
        showPoiLists(!listPoiCamera.isVisible());
    }

    /**
     * shows the lists at the top and bottom with an animation
     */
    private void showPoiLists(boolean show) {
        if (listPoiCamera.isVisible() == show) {
            return;
        } // nothing to do
        int animationDuration = 1000;
        if (!show) {
            AnimationUtils.slideUp(listPoiCamera, listPoiCamera.getHeight(), animationDuration, false);
            AnimationUtils.slideDown(listPoiMaps, listPoiMaps.getHeight(), animationDuration, false);
        } else {
            AnimationUtils.slideUp(listPoiMaps, listPoiMaps.getHeight(), animationDuration, true);
            AnimationUtils.slideDown(listPoiCamera, listPoiCamera.getHeight(), animationDuration, true);
        }
    }

    /**
     * toggles the visibility of the debuglog
     */
    @Override
    public void toggleDebugLog() {
        showDebugLog(!debugLogView.isVisible());
    }

    private RotateTransition    infoboxRotateTransition    = null;
    private TranslateTransition infoboxTranslateTransition = null;

    /**
     * initalizes all bindings to the properties in the viewmodel
     */
    private void initBindings() {
        ImageView cameraImage = new ImageView();
        cameraImage.imageProperty().bindBidirectional(viewModel.propertyCameraImageProperty());

        try {
            infoBoxView.getCloseButton().visibleProperty().bind(Bindings.equal(viewModel.getExpandedPOI().nameProperty(), "").not());
            infoBoxView.getCloseButton().onActionProperty().bindBidirectional(viewModel.propertyCloseButtonProperty());
            debugLogView.getButtonGpsLog().onActionProperty().bindBidirectional(viewModel.propertyToggleGpsButtonProperty());
            debugLogView.getButtonPoiLog().onActionProperty().bindBidirectional(viewModel.propertyTogglePoiButtonProperty());
            debugLogView.getButtonUserTrackingLog().onActionProperty().bindBidirectional(viewModel.propertyToggleUserTrackingButtonProperty());
            debugLogView.getButtonLandscapeTrackingLog().onActionProperty().bindBidirectional(viewModel.propertyToggleLandscapeTrackingButtonProperty());
            debugLogView.getButtonImageAnalysisLog().onActionProperty().bindBidirectional(viewModel.propertyToggleImageAnalysisButtonProperty());
            debugLogView.getButtonInformationSourceLog().onActionProperty().bindBidirectional(viewModel.propertyToggleInformationSourceButtonProperty());
            debugLogView.getButtonApplicationViewLog().onActionProperty().bindBidirectional(viewModel.propertyToggleApplicationViewButtonProperty());
            infoBoxView.getTitle().textProperty().bindBidirectional(viewModel.getExpandedPOI().nameProperty());
            infoBoxView.getImage().imageProperty().bindBidirectional(viewModel.getExpandedPOI().imageProperty());
            infoBoxView.getInformation().textProperty().bindBidirectional(viewModel.getExpandedPOI().informationAbstractProperty());
            listModuleStatus.itemsProperty().bindBidirectional(viewModel.listModuleStatusProperty());
            listExpressionStatus.itemsProperty().bindBidirectional(viewModel.listExpressionStatusProperty());
            listPoiCamera.itemsProperty().bindBidirectional(viewModel.propertyPoiCameraProperty());
            listPoiMaps.itemsProperty().bindBidirectional(viewModel.propertyPoiMapsProperty());

            //listDebugLog.itemsProperty().bindBidirectional(viewModel.propertyDebugLogProperty());
            debugLogView.getListView().itemsProperty().bind(viewModel.propertyDebugLogProperty());
            labelTensorFlowDebug.textProperty().bindBidirectional(DebugTF.logString);
            if (!AppProperties.getInstance().useDemoVideo) {
                stackPaneRoot.backgroundProperty().bindBidirectional(viewModel.getBackgroundProperty());
            }

            if (AppProperties.getInstance().useAnimations) {
                infoboxRotateTransition = new RotateTransition(Duration.seconds(0.75), infoBoxView);
                infoboxRotateTransition.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent AE) {
                        infoboxRotateTransition.setToAngle(viewModel.getInfoBoxRotation().doubleValue());
                        infoboxRotateTransition.play();
                    }
                });

                infoboxRotateTransition.setToAngle(viewModel.getInfoBoxRotation().doubleValue());
                infoboxRotateTransition.play();

                infoboxTranslateTransition = new TranslateTransition(Duration.seconds(2), infoBoxView);
                infoboxTranslateTransition.setOnFinished(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent AE) {
                        infoboxTranslateTransition.setToX(viewModel.getInfoBoxTranslationX().doubleValue());
                        infoboxTranslateTransition.setToY(viewModel.getInfoBoxTranslationY().doubleValue());
                        infoboxTranslateTransition.play();
                    }
                });

                infoboxTranslateTransition.setToX(viewModel.getInfoBoxTranslationX().doubleValue());
                infoboxTranslateTransition.setToY(viewModel.getInfoBoxTranslationY().doubleValue());
                infoboxTranslateTransition.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * initalizes cellfactories for all lists in the UI
     */
    private void initCellFactories() {
        //cell factory for the lists at the top and at the bottom
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

                            Label lblName = new Label(item.getName());
                            lblName.setFont(fontItem);
                            lblName.setStyle("" +
                                    "-fx-text-fill: white;" +
                                    "-fx-background-color: transparent; " +
                                    "-fx-effect: dropshadow( gaussian , black , 8, 0.90 , 0 , 0 );");

                            BorderPane.setMargin(lblName, new Insets(4, 0, 0, 0));
                            BorderPane.setAlignment(lblName, Pos.TOP_CENTER);

                            BorderPane border = new BorderPane();
                            border.setMinWidth(itemWidth);
                            border.setMaxWidth(itemWidth);
                            border.setOnMouseClicked(event -> viewModel.expandPoi(item.getId()));
                            border.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            border.setStyle("-fx-border-color: aliceblue; -fx-border-width: 4px;");
                            //border.setPadding(new Insets(4, 0, 4, 0));

                            if (item.getImage() != null) {
                                Background background = new Background(new BackgroundImage(item.getImage(),
                                        BackgroundRepeat.NO_REPEAT,
                                        BackgroundRepeat.NO_REPEAT,
                                        BackgroundPosition.CENTER,
                                        new BackgroundSize(100, 100,
                                                true,
                                                true,
                                                true,
                                                true)));
                                border.setBackground(background);
                            }

                            border.setTop(lblName);
                            setGraphic(border);
                        }
                    }
                };
            }
        };
        listPoiCamera.setCellFactory(callback);
        listPoiMaps.setCellFactory(callback);

        Callback<ListView<String>, ListCell<String>> logCallBack = param -> new LogTextCell();
        debugLogView.getListView().setCellFactory(logCallBack);

        //cellfactory for the module status list on the outer left
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

        //cellfactory for the list on the inner left
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

    /**
     * creates the styles for the red cross and the green checkmarks for the status lists
     */
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

    /**
     * creates a timeout indicator on the infobox
     */
    @Override
    public void showInfoBoxHideIndicator(int duration) {
        infoBoxView.getIndicator().setVisible(true);
        new Thread(() -> {
            int progress = 100;
            while (progress > 0) {
                int finalProgress = progress;
                Platform.runLater(() -> infoBoxView.getIndicator().setProgress(finalProgress));
                progress -= 1;
                try {
                    Thread.sleep(duration / 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * shows or hides the infobox in the middle of the screen with an animation
     */
    @Override
    public void showExpandedPoi(boolean show) {
        if (show) {
            if (infoBoxView.isVisible()) {
                return;
            } // already visible, nothing to do

            AnimationUtils.scaleUp(infoBoxView, 0, 1, 0 ,1, 350, true);
        } else {
            AnimationUtils.scaleDown(infoBoxView, 0, 0, 350, false);
            infoBoxView.getIndicator().setVisible(false);
        }
    }

    /**
     * stops the program
     */
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
        return mediaLayer;
    }

    /**
     * class for an entry in the debuglog
     */
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

    @Override
    public void showDebugLayer() {
        AnimationUtils.fadeIn(debugLayer, 1500);
    }

    @Override
    public void hideDebugLayer() {
        AnimationUtils.fadeOut(debugLayer, 1500);
    }

    @Override
    public void showApplicationLayer() {
        AnimationUtils.fadeIn(applicationLayer, 1500);
    }

    @Override
    public void hideApplicationLayer() {
        AnimationUtils.fadeOut(applicationLayer, 1500);
    }



}
