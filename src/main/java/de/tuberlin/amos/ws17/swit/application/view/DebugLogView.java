package de.tuberlin.amos.ws17.swit.application.view;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DebugLogView extends GridPane {

    private Button           buttonUserTrackingLog      = null;
    private Button           buttonLandscapeTrackingLog = null;
    private Button           buttonPoiLog               = null;
    private Button           buttonInformationSourceLog = null;
    private Button           buttonImageAnalysisLog     = null;
    private Button           buttonApplicationViewLog   = null;
    private Button           buttonGpsLog               = null;
    private ListView<String> listView                   = null;


    public DebugLogView() {
        setupViews();
    }

    private void setupViews() {
        buttonApplicationViewLog = new Button("application_view");
        buttonApplicationViewLog.setId("toggleApplicationView");
        buttonApplicationViewLog.setOnMouseClicked(event -> toggleStyle(DebugLog.applicationView));
        buttonApplicationViewLog.getStyleClass().add("toggleButton");
        buttonApplicationViewLog.setMaxWidth(Double.MAX_VALUE);

        buttonGpsLog = new Button("GPS");
        buttonGpsLog.setId("toggleGpsLog");
        buttonGpsLog.setOnMouseClicked(event -> toggleStyle(DebugLog.gps));
        buttonGpsLog.setMaxWidth(Double.MAX_VALUE);

        buttonImageAnalysisLog = new Button("image_analysis");
        buttonImageAnalysisLog.setId("toggleImageAnalysis");
        buttonImageAnalysisLog.setOnMouseClicked(event -> toggleStyle(DebugLog.imageAnalysis));
        buttonImageAnalysisLog.setMaxWidth(Double.MAX_VALUE);

        buttonInformationSourceLog = new Button("information_source");
        buttonInformationSourceLog.setId("toggleInformationSource");
        buttonInformationSourceLog.setOnMouseClicked(event -> toggleStyle(DebugLog.informationSource));
        buttonInformationSourceLog.setMaxWidth(Double.MAX_VALUE);

        buttonLandscapeTrackingLog = new Button("landscape_tracking");
        buttonLandscapeTrackingLog.setId("toggleLandscapeTracking");
        buttonLandscapeTrackingLog.setOnMouseClicked(event -> toggleStyle(DebugLog.landscapeTracking));
        buttonLandscapeTrackingLog.setMaxWidth(Double.MAX_VALUE);

        buttonPoiLog = new Button("POI");
        buttonPoiLog.setId("togglePoi");
        buttonPoiLog.setOnMouseClicked(event -> toggleStyle(DebugLog.poi));
        buttonPoiLog.setMaxWidth(Double.MAX_VALUE);

        buttonUserTrackingLog = new Button("user_tracking");
        buttonUserTrackingLog.setId("toggleUserTracking");
        buttonUserTrackingLog.setOnMouseClicked(event -> toggleStyle(DebugLog.userTracking));
        buttonUserTrackingLog.setMaxWidth(Double.MAX_VALUE);

        VBox buttonContainer = new VBox();
        buttonContainer.setId("togglePane");

        buttonContainer.getChildren().add(buttonApplicationViewLog);
        buttonContainer.getChildren().add(buttonGpsLog);
        buttonContainer.getChildren().add(buttonImageAnalysisLog);
        buttonContainer.getChildren().add(buttonInformationSourceLog);
        buttonContainer.getChildren().add(buttonLandscapeTrackingLog);
        buttonContainer.getChildren().add(buttonPoiLog);
        buttonContainer.getChildren().add(buttonUserTrackingLog);

        listView = new ListView<>();
        listView.setId("listDebugLog");

        setId("debugPane");
        setVisible(AppProperties.getInstance().useDebugLog);

        getColumnConstraints().add(new ColumnConstraints());
        getColumnConstraints().add(new ColumnConstraints());
        getColumnConstraints().get(0).setPercentWidth(15);
        getColumnConstraints().get(1).setPercentWidth(85);

        add(buttonContainer, 0, 0);
        add(listView, 1, 0);
    }

    private void toggleStyle(int module) {
        boolean status = DebugLog.getModuleStatus(module);
        if (module == DebugLog.gps) {
            if (status) buttonGpsLog.setStyle("-fx-background-color: transparent");
            else buttonGpsLog.setStyle("-fx-background-color: dimgray");
        }
        if (module == DebugLog.poi) {
            if (status) buttonPoiLog.setStyle("-fx-background-color: transparent");
            else buttonPoiLog.setStyle("-fx-background-color: dimgray");
        }
        if (module == DebugLog.userTracking) {
            if (status) buttonUserTrackingLog.setStyle("-fx-background-color: transparent");
            else buttonUserTrackingLog.setStyle("-fx-background-color: dimgray");
        }
        if (module == DebugLog.landscapeTracking) {
            if (status) buttonLandscapeTrackingLog.setStyle("-fx-background-color: transparent");
            else buttonLandscapeTrackingLog.setStyle("-fx-background-color: dimgray");
        }
        if (module == DebugLog.imageAnalysis) {
            if (status) buttonImageAnalysisLog.setStyle("-fx-background-color: transparent");
            else buttonImageAnalysisLog.setStyle("-fx-background-color: dimgray");
        }
        if (module == DebugLog.applicationView) {
            if (status) buttonApplicationViewLog.setStyle("-fx-background-color: transparent");
            else buttonApplicationViewLog.setStyle("-fx-background-color: dimgray");
        }
        if (module == DebugLog.informationSource) {
            if (status) buttonInformationSourceLog.setStyle("-fx-background-color: transparent");
            else buttonInformationSourceLog.setStyle("-fx-background-color: dimgray");
        }
    }

    public Button getButtonUserTrackingLog() {
        return buttonUserTrackingLog;
    }

    public Button getButtonLandscapeTrackingLog() {
        return buttonLandscapeTrackingLog;
    }

    public Button getButtonPoiLog() {
        return buttonPoiLog;
    }

    public Button getButtonInformationSourceLog() {
        return buttonInformationSourceLog;
    }

    public Button getButtonImageAnalysisLog() {
        return buttonImageAnalysisLog;
    }

    public Button getButtonApplicationViewLog() {
        return buttonApplicationViewLog;
    }

    public Button getButtonGpsLog() {
        return buttonGpsLog;
    }

    public ListView<String> getListView() {
        return listView;
    }
}
