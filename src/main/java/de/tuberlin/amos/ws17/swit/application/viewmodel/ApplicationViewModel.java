package de.tuberlin.amos.ws17.swit.application.viewmodel;

import de.tuberlin.amos.ws17.swit.common.Module;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;

import java.util.List;

public interface ApplicationViewModel {

    void onKeyPressed(KeyCode code);

    Property<Image> propertyCameraImageProperty();

    PoiViewModel getExpandedPOI();

    Property<EventHandler<ActionEvent>> propertyCloseButtonProperty();

    Property<EventHandler<ActionEvent>> propertyToggleGpsButtonProperty();
    Property<EventHandler<ActionEvent>> propertyTogglePoiButtonProperty();
    Property<EventHandler<ActionEvent>> propertyToggleLandscapeTrackingButtonProperty();
    Property<EventHandler<ActionEvent>> propertyToggleUserTrackingButtonProperty();
    Property<EventHandler<ActionEvent>> propertyToggleApplicationViewButtonProperty();
    Property<EventHandler<ActionEvent>> propertyToggleInformationSourceButtonProperty();
    Property<EventHandler<ActionEvent>> propertyToggleImageAnalysisButtonProperty();

    Property<ObservableList<ModuleStatusViewModel>> listModuleStatusProperty();

    Property<ObservableList<UserExpressionViewModel>> listExpressionStatusProperty();

    Property<ObservableList<PoiViewModel>> propertyPoiCameraProperty();

    Property<ObservableList<PoiViewModel>> propertyPoiMapsProperty();

    Property<ObservableList<String>> propertyDebugLogProperty();

    SimpleListProperty<String> propertyDebugLogTFProperty();

    Property<Background> getBackgroundProperty();

    boolean expandPoi(String id);

    List<Module> getModuleList();

    void setRunning(boolean running);

    SimpleDoubleProperty getInfoBoxRotation();
    SimpleDoubleProperty getInfoBoxTranslationX();
    SimpleDoubleProperty getInfoBoxTranslationY();
}
