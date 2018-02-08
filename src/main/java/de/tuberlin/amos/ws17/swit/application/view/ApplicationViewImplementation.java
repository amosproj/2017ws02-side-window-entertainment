package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.application.viewmodel.*;
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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
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
    private MediaView mediaLayer = null;
    private MediaPlayer mediaPlayer = null;
    //##########

    //##### application layer #####
    private GridPane applicationLayer = null;
    //### infobox
    private BorderPane infoboxPane = null;
    private BorderPane infoboxTitlePane = null;
    private BorderPane infoboxContentPane = null;
    private Button infoboxCloseButton = null;
    private Label infoboxTitle = null;
    private Label infoboxInformation = null;
    private ImageView infoboxImage = null;
    private ScrollPane infoboxScrollPane = null;

    //### Poi Lists
    private ListView<PoiViewModel> listPoiCamera = null;
    private ListView<PoiViewModel> listPoiMaps = new ListView<>();

    //### Status: expressions and module status
    private HBox statusPane = null;
    private ListView<ModuleStatusViewModel> listModuleStatus = new ListView<>();
    private ListView<UserExpressionViewModel> listExpressionStatus = new ListView<>();
    //##########

    //##### debug layer #####
    private GridPane debugLayer = null;
    //### DebugLog
    private ListView<String> listDebugLog = new ListView<>();

    private HBox debugLogPane = null;
    private VBox debugLogVBoxButtons = null;

    private Button buttonUserTrackingLog = null;
    private Button buttonLandscapeTrackingLog = null;
    private Button buttonPoiLog = null;
    private Button buttonInformationSourceLog = null;
    private Button buttonImageAnalysisLog = null;
    private Button buttonApplicationViewLog = null;
    private Button buttonGpsLog = null;
    //### TensorFlow Debug
    private Label labelTensorFlowDebug = new Label();
    //##########

    private static final String FONTNAME = "Helvetica Neue";
    private static double fontSizeTitle = 12;
    private static double fontSizeLabel = 12;
    private static double fontSizeText = 12;

    private static ApplicationViewModel viewModel;

    public void init() {

        initFontSizes();
        initRootStackPane();

        viewModel = new ApplicationViewModelImplementation(this);

        initBindings();
        initCellFactories();
    }

    private void initFontSizes() {
        Screen screen = Screen.getPrimary();
        Rectangle2D screenVisualBounds = screen.getVisualBounds();

        fontSizeTitle = screenVisualBounds.getHeight() * 0.04;
        fontSizeLabel = screenVisualBounds.getHeight() * 0.03;
        fontSizeText = screenVisualBounds.getHeight() * 0.02;
    }

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
    }

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
        }
        catch (Exception e) {
            DebugLog.log("MediaPlayer kann nicht initialisiert werden");
        }
    }

    private void initApplicationLayer() {
        applicationLayer = new GridPane();
        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(0).setPercentHeight(15.0);
        applicationLayer.getRowConstraints().get(0).setValignment(VPos.CENTER);

        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(1).setPercentHeight(70.0);
        applicationLayer.getRowConstraints().get(1).setValignment(VPos.CENTER);

        applicationLayer.getRowConstraints().add(new RowConstraints());
        applicationLayer.getRowConstraints().get(2).setPercentHeight(15.0);
        applicationLayer.getRowConstraints().get(2).setValignment(VPos.CENTER);

        int numColumns = 3;
        for(int i = 0;i < numColumns;i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / numColumns);
            cc.setHalignment(HPos.CENTER);
            applicationLayer.getColumnConstraints().add(cc);
        }

        initListPoiCamera();
        initListPoiMaps();
        initStatusPane();
        initInfoboxPane();

        applicationLayer.add(listPoiCamera, 0, 0, 3, 1);
        applicationLayer.add(listPoiMaps, 0,2, 3, 1);
        applicationLayer.add(statusPane, 0, 1);
        applicationLayer.add(infoboxPane, 1, 1);
    }

    private void initListPoiCamera() {
        listPoiCamera = new ListView<>();
        listPoiCamera.setId("listPoiCamera");
    }

    private void initListPoiMaps() {
        listPoiMaps = new ListView<>();
        listPoiMaps.setId("listPoiMaps");
    }

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

    private void initInfoboxPane() {
        infoboxTitle = new Label();
        infoboxTitle.setId("expansionName");
        infoboxTitle.setAlignment(Pos.TOP_CENTER);
        infoboxTitle.setFont(new Font(FONTNAME, fontSizeTitle));

        infoboxCloseButton = new Button("X");
        infoboxCloseButton.setId("expansionButton");

        infoboxTitlePane = new BorderPane();
        infoboxTitlePane.setId("expansionTopPane");
        infoboxTitlePane.setLeft(infoboxTitle);
        infoboxTitlePane.setRight(infoboxCloseButton);

        infoboxImage = new ImageView();
        infoboxImage.setId("expansionImage");
        BorderPane.setAlignment(infoboxImage, Pos.CENTER);
        infoboxImage.setPreserveRatio(true);
        infoboxImage.setFitHeight(150);
        infoboxImage.minHeight(0);
        infoboxImage.minWidth(0);

        infoboxInformation = new Label();
        infoboxInformation.setId("expansionInformation");
        infoboxInformation.setAlignment(Pos.TOP_CENTER);
        infoboxInformation.setFont(new Font(FONTNAME, fontSizeText));

        infoboxContentPane = new BorderPane();
        infoboxContentPane.setId("expansionContentPane");
        infoboxContentPane.setTop(infoboxImage);
        infoboxContentPane.setBottom(infoboxInformation);

        infoboxScrollPane = new ScrollPane();
        infoboxScrollPane.setId("expansionScrollPane");
        infoboxScrollPane.setContent(infoboxContentPane);
        infoboxScrollPane.setFitToWidth(true);

        infoboxPane = new BorderPane();
        infoboxPane.setId("expansionPane");
        infoboxPane.setVisible(false);
        BorderPane.setAlignment(infoboxPane, Pos.CENTER);
        infoboxPane.setTop(infoboxTitlePane);
        infoboxPane.setCenter(infoboxImage);
        infoboxPane.setBottom(infoboxScrollPane);
    }

    private void initDebugLayer() {
        debugLayer = new GridPane();
        debugLayer.getRowConstraints().addAll(applicationLayer.getRowConstraints());
        debugLayer.getColumnConstraints().addAll(applicationLayer.getColumnConstraints());
        debugLayer.setPickOnBounds(false);

        initDebugLog();

        initDebugTensorFlow();

        applicationLayer.add(debugLogPane, 2, 1, 2, 1);
        applicationLayer.add(labelTensorFlowDebug, 2, 2);
    }

    private void initDebugLog() {
        debugLogVBoxButtons = new VBox();
        debugLogVBoxButtons.setId("togglePane");

        buttonUserTrackingLog = new Button("user_tracking");
        buttonUserTrackingLog.setId("toggleUserTracking");
        buttonUserTrackingLog.setOnMouseClicked(event -> toggleStyle("UserTracking"));

        buttonLandscapeTrackingLog = new Button("landscape_tracking");
        buttonLandscapeTrackingLog.setId("toggleLandscapeTracking");
        buttonLandscapeTrackingLog.setOnMouseClicked(event -> toggleStyle("LandscapeTracking"));

        buttonPoiLog = new Button("POI");
        buttonPoiLog.setId("togglePoi");
        buttonPoiLog.setOnMouseClicked(event -> toggleStyle("POI"));

        buttonInformationSourceLog = new Button("information_source");
        buttonInformationSourceLog.setId("toggleInformationSource");
        buttonInformationSourceLog.setOnMouseClicked(event -> toggleStyle("InformationSource"));

        buttonImageAnalysisLog = new Button("image_analysis");
        buttonImageAnalysisLog.setId("toggleImageAnalysis");
        buttonImageAnalysisLog.setOnMouseClicked(event -> toggleStyle("ImageAnalysis"));

        buttonApplicationViewLog = new Button("application_view");
        buttonApplicationViewLog.setId("toggleApplicationView");
        buttonApplicationViewLog.setOnMouseClicked(event -> toggleStyle("ApplicationView"));
        buttonApplicationViewLog.getStyleClass().add("toggleButton");

        buttonGpsLog = new Button("GPS");
        buttonGpsLog.setId("toggleGpsLog");
        buttonGpsLog.setOnMouseClicked(event -> toggleStyle("GPS"));

        debugLogVBoxButtons.getChildren().add(buttonApplicationViewLog);
        debugLogVBoxButtons.getChildren().add(buttonGpsLog);
        debugLogVBoxButtons.getChildren().add(buttonImageAnalysisLog);
        debugLogVBoxButtons.getChildren().add(buttonInformationSourceLog);
        debugLogVBoxButtons.getChildren().add(buttonLandscapeTrackingLog);
        debugLogVBoxButtons.getChildren().add(buttonPoiLog);
        debugLogVBoxButtons.getChildren().add(buttonUserTrackingLog);

        listDebugLog = new ListView<>();
        listDebugLog.setId("listDebugLog");

        debugLogPane = new HBox();
        debugLogPane.setId("debugPane");

        debugLogPane.getChildren().add(debugLogVBoxButtons);
        debugLogPane.getChildren().add(listDebugLog);
    }

    private void initDebugTensorFlow() {
        labelTensorFlowDebug = new Label();
        labelTensorFlowDebug.setId("textTFDebug");
        labelTensorFlowDebug.setVisible(false);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Side Window Infotainment");

        Scene scene = new Scene(stackPaneRoot, 800, 600, Color.WHITE);
        scene.setOnKeyPressed(event -> viewModel.onKeyPressed(event.getCode()));
        scene.getStylesheets().add("/stylesheets/TransparentApplicationViewStylesheet.css");
        stage.setScene(scene);
        initFullscreenMode(stage);
        stage.show();
    }

    private void initFullscreenMode(Stage stage) {
        if (AppProperties.getInstance().useFullscreen) {
            stage.setMaximized(true);
        }
        else if (AppProperties.getInstance().useFullscreenWithoutWindowChrome) {
            stage.setFullScreen(true);
        }
    }

    @Override
    public void toggleTensorFlowDebugWindow() {
        labelTensorFlowDebug.setVisible(!labelTensorFlowDebug.isVisible());
    }

    @Override
    public void showDebugLog(boolean show) {
        debugLogPane.setVisible(show);
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
        showDebugLog(!debugLogPane.isVisible());
    }

    private RotateTransition infoboxRotateTransition = null;
    private TranslateTransition infoboxTranslateTransition = null;
    //TODO : private ScaleTransition infoboxScaleTransition = new ScaleTransition(Duration.seconds(2), infoboxPane);

    private void initBindings() {
        ImageView cameraImage = new ImageView();
        cameraImage.imageProperty().bindBidirectional(viewModel.propertyCameraImageProperty());

        try {
            infoboxCloseButton.visibleProperty().bind(Bindings.equal(viewModel.getExpandedPOI().nameProperty(), "").not());
            infoboxCloseButton.onActionProperty().bindBidirectional(viewModel.propertyCloseButtonProperty());
            buttonGpsLog.onActionProperty().bindBidirectional(viewModel.propertyToggleGpsButtonProperty());
            buttonPoiLog.onActionProperty().bindBidirectional(viewModel.propertyTogglePoiButtonProperty());
            buttonUserTrackingLog.onActionProperty().bindBidirectional(viewModel.propertyToggleUserTrackingButtonProperty());
            buttonLandscapeTrackingLog.onActionProperty().bindBidirectional(viewModel.propertyToggleLandscapeTrackingButtonProperty());
            buttonImageAnalysisLog.onActionProperty().bindBidirectional(viewModel.propertyToggleImageAnalysisButtonProperty());
            buttonInformationSourceLog.onActionProperty().bindBidirectional(viewModel.propertyToggleInformationSourceButtonProperty());
            buttonApplicationViewLog.onActionProperty().bindBidirectional(viewModel.propertyToggleApplicationViewButtonProperty());
            infoboxTitle.textProperty().bindBidirectional(viewModel.getExpandedPOI().nameProperty());
            infoboxImage.imageProperty().bindBidirectional(viewModel.getExpandedPOI().imageProperty());
            infoboxInformation.textProperty().bindBidirectional(viewModel.getExpandedPOI().informationAbstractProperty());
            listModuleStatus.itemsProperty().bindBidirectional(viewModel.listModuleStatusProperty());
            listExpressionStatus.itemsProperty().bindBidirectional(viewModel.listExpressionStatusProperty());
            listPoiCamera.itemsProperty().bindBidirectional(viewModel.propertyPoiCameraProperty());
            listPoiMaps.itemsProperty().bindBidirectional(viewModel.propertyPoiMapsProperty());

            listDebugLog.itemsProperty().bindBidirectional(viewModel.propertyDebugLogProperty());
            labelTensorFlowDebug.textProperty().bindBidirectional(DebugTF.logString);
            if (!AppProperties.getInstance().useDemoVideo) {
                stackPaneRoot.backgroundProperty().bindBidirectional(viewModel.getBackgroundProperty());
            }

            infoboxRotateTransition = new RotateTransition(Duration.seconds(0.75), infoboxPane);
            infoboxRotateTransition.setOnFinished(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent AE){
                    infoboxRotateTransition.setToAngle(viewModel.getInfoBoxRotation().doubleValue());
                    infoboxRotateTransition.play();
                }
            });

            infoboxRotateTransition.setToAngle(viewModel.getInfoBoxRotation().doubleValue());
            infoboxRotateTransition.play();

            infoboxTranslateTransition = new TranslateTransition(Duration.seconds(2), infoboxPane);
            infoboxTranslateTransition.setOnFinished(new EventHandler<ActionEvent>(){
                public void handle(ActionEvent AE){
                    infoboxTranslateTransition.setToX(viewModel.getInfoBoxTranslationX().doubleValue());
                    infoboxTranslateTransition.setToY(viewModel.getInfoBoxTranslationY().doubleValue());
                    infoboxTranslateTransition.play();
                }
            });

            infoboxTranslateTransition.setToX(viewModel.getInfoBoxTranslationX().doubleValue());
            infoboxTranslateTransition.setToY(viewModel.getInfoBoxTranslationY().doubleValue());
            infoboxTranslateTransition.play();
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
                            GridPane pane = new GridPane();
                            ImageView imageView = new ImageView(item.getImage());
                            imageView.setPreserveRatio(true);
                            imageView.fitHeightProperty().bind(listPoiCamera.heightProperty().subtract(25));
                            RowConstraints rc = new RowConstraints();
                            rc.setPercentHeight(100.0);
                            rc.setValignment(VPos.TOP);
                            pane.getRowConstraints().add(rc);
                            ColumnConstraints cc = new ColumnConstraints();
                            cc.setPercentWidth(100.0);
                            cc.setHalignment(HPos.CENTER);
                            pane.getColumnConstraints().add(cc);
                            Label lblName = new Label(item.getName());
                            lblName.setPadding(new Insets(0, 5, 0, 5));
                            lblName.setFont(new Font(FONTNAME, 13));
                            lblName.setStyle("-fx-text-fill: black;" +
                                    "-fx-background-color: transparent; -fx-effect: dropshadow( gaussian , white , 2, 1.0 , 0 , 0 );");
                            pane.add(imageView, 0, 0);
                            pane.add(lblName, 0,0);
                            pane.setOnMouseClicked(event -> viewModel.expandPoi(item.getId()));
                            pane.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                            pane.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3px;");
                            pane.setPadding(new Insets(5, 0, 5, 0));
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

    @Override
    public void showExpandedPoi(boolean show) {
        infoboxPane.setVisible(show);
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
        return mediaLayer;
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

    private void toggleStyle(String module){
        boolean status = DebugLog.getModuleStatus(module);
        if (module.equals("GPS")){
            if (status) buttonGpsLog.setStyle("-fx-background-color: black");
            else        buttonGpsLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("POI")){
            if (status) buttonPoiLog.setStyle("-fx-background-color: black");
            else        buttonPoiLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("UserTracking")){
            if (status) buttonUserTrackingLog.setStyle("-fx-background-color: black");
            else        buttonUserTrackingLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("LandscapeTracking")){
            if (status) buttonLandscapeTrackingLog.setStyle("-fx-background-color: black");
            else        buttonLandscapeTrackingLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("ImageAnalysis")){
            if (status) buttonImageAnalysisLog.setStyle("-fx-background-color: black");
            else        buttonImageAnalysisLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("ApplicationView")){
            if (status) buttonApplicationViewLog.setStyle("-fx-background-color: black");
            else        buttonApplicationViewLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("InformationSource")){
            if (status) buttonInformationSourceLog.setStyle("-fx-background-color: black");
            else        buttonInformationSourceLog.setStyle("-fx-background-color: dimgray");
        }
    }

}
