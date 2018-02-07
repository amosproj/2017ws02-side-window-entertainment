package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.application.viewmodel.*;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.DebugTF;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
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

public class ApplicationViewImplementation extends Application implements ApplicationView {

    private BorderPane                        pnFoundation         = new BorderPane();
    private ListView<PoiViewModel>            listPoiCamera        = new ListView<>();
    private ListView<PoiViewModel>            listPoiMaps          = new ListView<>();
    private ListView<String>                  listDebugLog         = new ListView<>();
    private Label                             textTFDebug          = new Label();
    private ListView<ModuleStatusViewModel>   listModuleStatus     = new ListView<>();
    private ListView<UserExpressionViewModel> listExpressionStatus = new ListView<>();

    // DebugLog Buttons
    private Button toggleUserTrackingLog      = new Button("user_tracking");
    private Button toggleLandscapeTrackingLog = new Button("landscape_tracking");
    private Button togglePoiLog               = new Button("POI");
    private Button toggleInformationSourceLog = new Button("information_source");
    private Button toggleImageAnalysisLog     = new Button("image_analysis");
    private Button toggleApplicationViewLog   = new Button("application_view");
    private Button toggleGpsLog               = new Button("GPS");

    private BorderPane  infoboxPane        = new BorderPane();
    private BorderPane  infoboxTitlePane     = new BorderPane();
    private BorderPane  infoboxContentPane = new BorderPane();
    private Button      infoboxCloseButton      = new Button("X");
    private Label       infoboxTitle        = new Label();
    private Label       infoboxInformation = new Label();
    private ImageView   infoboxImage       = new ImageView();
    private ScrollPane  infoboxScrollPane  = new ScrollPane();
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
        initFullscreenMode(stage);
        stage.show();
    }

    private void initFullscreenMode(Stage stage) {
        if (AppProperties.getInstance().useFullscreen) {
            Screen screen = Screen.getPrimary();
            Rectangle2D screenVisualBounds = screen.getVisualBounds();

            stage.setX(screenVisualBounds.getMinX());
            stage.setY(screenVisualBounds.getMinY());
            stage.setWidth(screenVisualBounds.getWidth());
            stage.setHeight(screenVisualBounds.getHeight());
        }
        else if (AppProperties.getInstance().useFullscreenWithoutWindowChrome) {
            stage.setFullScreen(true);
        }
    }

    @Override
    public void toggleTensorFlowDebugWindow() {
        textTFDebug.setVisible(!textTFDebug.isVisible());
    }

    private void initElements() {
        GridPane lists = new GridPane();
        int numRows = 3;
        for(int i = 0;i < numRows;i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / numRows);//-55.0*i*i + 110*i + 15.0
            rc.setValignment(VPos.CENTER);
            lists.getRowConstraints().add(rc);
        }
        lists.getRowConstraints().get(0).setPercentHeight(15.0);
        lists.getRowConstraints().get(1).setPercentHeight(70.0);
        lists.getRowConstraints().get(2).setPercentHeight(15.0);

        int numColumns = 3;
        for(int i = 0;i < numColumns;i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / numColumns);
            cc.setHalignment(HPos.CENTER);
            lists.getColumnConstraints().add(cc);
        }
        lists.add(listPoiCamera, 0, 0, 3, 1);
        lists.add(listPoiMaps, 0,2, 3, 1);
        lists.add(statusPane, 0, 1);
        lists.add(infoboxPane, 1, 1);
        lists.add(debugPane, 2, 1);

        /*pnFoundation.setId("pnFoundation");
        pnFoundation.setTop(listPoiCamera);
        pnFoundation.setBottom(listPoiMaps);
        pnFoundation.setLeft(statusPane);*/

        listPoiCamera.setId("listPoiCamera");
        listPoiMaps.setId("listPoiMaps");

        statusPane.setId("statusPane");
        statusPane.getChildren().add(listModuleStatus);
        statusPane.getChildren().add(listExpressionStatus);

        listModuleStatus.setId("listModuleStatus");

        listExpressionStatus.setId("listExpressionStatus");

        togglePane.setId("togglePane");
        toggleApplicationViewLog.setId("toggleApplicationView");
        toggleApplicationViewLog.setOnMouseClicked(event -> toggleStyle("ApplicationView"));
        toggleImageAnalysisLog.setId("toggleImageAnalysis");
        toggleImageAnalysisLog.setOnMouseClicked(event -> toggleStyle("ImageAnalysis"));
        toggleInformationSourceLog.setId("toggleInformationSource");
        toggleInformationSourceLog.setOnMouseClicked(event -> toggleStyle("InformationSource"));
        toggleLandscapeTrackingLog.setId("toggleLandscapeTracking");
        toggleLandscapeTrackingLog.setOnMouseClicked(event -> toggleStyle("LandscapeTracking"));
        togglePoiLog.setId("togglePoi");
        togglePoiLog.setOnMouseClicked(event -> toggleStyle("POI"));
        toggleUserTrackingLog.setId("toggleUserTracking");
        toggleUserTrackingLog.setOnMouseClicked(event -> toggleStyle("UserTracking"));
        toggleGpsLog.setId("toggleGpsLog");
        toggleGpsLog.setOnMouseClicked(event -> toggleStyle("GPS"));

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
        textTFDebug.setVisible(false);
        infoboxPane.setId("expansionPane");
        infoboxPane.setVisible(false);
        infoboxTitlePane.setId("expansionTopPane");
        infoboxCloseButton.setId("expansionButton");
        infoboxTitle.setId("expansionName");
        infoboxContentPane.setId("expansionContentPane");
        infoboxInformation.setId("expansionInformation");
        infoboxImage.setId("expansionImage");
        infoboxScrollPane.setId("expansionScrollPane");

        mediaView = new MediaView(mediaPlayer);
        mediaView.setVisible(false);
        final DoubleProperty width = mediaView.fitWidthProperty();
        final DoubleProperty height = mediaView.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        mediaView.setPreserveRatio(true);

        root.getChildren().add(mediaView);
        //root.getChildren().add(pnFoundation);
        root.getChildren().add(lists);
        // root.getChildren().add(listDebugLog);
        // StackPane.setAlignment(listDebugLog, Pos.TOP_RIGHT);
        //root.getChildren().add(debugPane);
        //StackPane.setAlignment(debugPane, Pos.TOP_RIGHT);
        root.getChildren().add(textTFDebug);
        StackPane.setAlignment(textTFDebug, Pos.BOTTOM_RIGHT);
        Insets insets = new Insets(0, 0, 150, 0);
        StackPane.setMargin(textTFDebug, insets);
    }

    private void initExpansion() {
        infoboxTitle.setAlignment(Pos.TOP_CENTER);
        infoboxInformation.setAlignment(Pos.TOP_CENTER);
        infoboxContentPane.setTop(infoboxImage);
        infoboxContentPane.setBottom(infoboxInformation);
        infoboxScrollPane.setContent(infoboxContentPane);
        infoboxImage.setPreserveRatio(true);
        infoboxImage.setFitHeight(150);
        infoboxImage.minHeight(0);
        infoboxImage.minWidth(0);

        infoboxTitlePane.setLeft(infoboxTitle);
        infoboxTitlePane.setRight(infoboxCloseButton);
        infoboxPane.setTop(infoboxTitlePane);
        infoboxPane.setCenter(infoboxImage);
        infoboxPane.setBottom(infoboxScrollPane);
        //pnFoundation.setCenter(infoboxPane);
        BorderPane.setAlignment(infoboxPane, Pos.CENTER);
        BorderPane.setAlignment(infoboxImage, Pos.CENTER);
        infoboxScrollPane.setFitToWidth(true);
    }

    @Override
    public void showDebugLog(boolean show) {
        debugPane.setVisible(show);
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
        showDebugLog(!debugPane.isVisible());
    }

    private void initBindings() {
        ImageView cameraImage = new ImageView();
        cameraImage.imageProperty().bindBidirectional(viewModel.propertyCameraImageProperty());

        try {
            infoboxCloseButton.visibleProperty().bind(Bindings.equal(viewModel.getExpandedPOI().nameProperty(), "").not());
            infoboxCloseButton.onActionProperty().bindBidirectional(viewModel.propertyCloseButtonProperty());
            toggleGpsLog.onActionProperty().bindBidirectional(viewModel.propertyToggleGpsButtonProperty());
            togglePoiLog.onActionProperty().bindBidirectional(viewModel.propertyTogglePoiButtonProperty());
            toggleUserTrackingLog.onActionProperty().bindBidirectional(viewModel.propertyToggleUserTrackingButtonProperty());
            toggleLandscapeTrackingLog.onActionProperty().bindBidirectional(viewModel.propertyToggleLandscapeTrackingButtonProperty());
            toggleImageAnalysisLog.onActionProperty().bindBidirectional(viewModel.propertyToggleImageAnalysisButtonProperty());
            toggleInformationSourceLog.onActionProperty().bindBidirectional(viewModel.propertyToggleInformationSourceButtonProperty());
            toggleApplicationViewLog.onActionProperty().bindBidirectional(viewModel.propertyToggleApplicationViewButtonProperty());
            infoboxTitle.textProperty().bindBidirectional(viewModel.getExpandedPOI().nameProperty());
            infoboxImage.imageProperty().bindBidirectional(viewModel.getExpandedPOI().imageProperty());
            infoboxInformation.textProperty().bindBidirectional(viewModel.getExpandedPOI().informationAbstractProperty());
            listModuleStatus.itemsProperty().bindBidirectional(viewModel.listModuleStatusProperty());
            listExpressionStatus.itemsProperty().bindBidirectional(viewModel.listExpressionStatusProperty());
            listPoiCamera.itemsProperty().bindBidirectional(viewModel.propertyPoiCameraProperty());
            listPoiMaps.itemsProperty().bindBidirectional(viewModel.propertyPoiMapsProperty());

            listDebugLog.itemsProperty().bindBidirectional(viewModel.propertyDebugLogProperty());
            textTFDebug.textProperty().bindBidirectional(DebugTF.logString);
            if (!AppProperties.getInstance().useDemoVideo) {
                pnFoundation.backgroundProperty().bindBidirectional(viewModel.getBackgroundProperty());
            }

            infoboxPane.rotateProperty().bindBidirectional(viewModel.getInfoBoxRotation());
            infoboxPane.translateXProperty().bindBidirectional(viewModel.getInfoBoxTranslationX());
            infoboxPane.translateYProperty().bindBidirectional(viewModel.getInfoBoxTranslationY());
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

    private void toggleStyle(String module){
        boolean status = DebugLog.getModuleStatus(module);
        if (module.equals("GPS")){
            if (status) toggleGpsLog.setStyle("-fx-background-color: black");
            else        toggleGpsLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("POI")){
            if (status) togglePoiLog.setStyle("-fx-background-color: black");
            else        togglePoiLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("UserTracking")){
            if (status) toggleUserTrackingLog.setStyle("-fx-background-color: black");
            else        toggleUserTrackingLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("LandscapeTracking")){
            if (status) toggleLandscapeTrackingLog.setStyle("-fx-background-color: black");
            else        toggleLandscapeTrackingLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("ImageAnalysis")){
            if (status) toggleImageAnalysisLog.setStyle("-fx-background-color: black");
            else        toggleImageAnalysisLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("ApplicationView")){
            if (status) toggleApplicationViewLog.setStyle("-fx-background-color: black");
            else        toggleApplicationViewLog.setStyle("-fx-background-color: dimgray");
        }
        if (module.equals("InformationSource")){
            if (status) toggleInformationSourceLog.setStyle("-fx-background-color: black");
            else        toggleInformationSourceLog.setStyle("-fx-background-color: dimgray");
        }
    }

}
