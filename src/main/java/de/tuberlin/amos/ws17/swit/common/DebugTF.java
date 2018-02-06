package de.tuberlin.amos.ws17.swit.common;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

public class DebugTF {

    public static SimpleStringProperty logString = new SimpleStringProperty();
    public static void log(String s) {
        Platform.runLater(() -> logString.setValue(s));
    }
}
