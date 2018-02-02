package de.tuberlin.amos.ws17.swit.common;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DebugTF {

    public static SimpleStringProperty logString = new SimpleStringProperty();
    public static void log(String s) {
        logString.setValue(s);
    }
}
