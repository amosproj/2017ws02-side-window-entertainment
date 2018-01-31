package de.tuberlin.amos.ws17.swit.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class DebugLogTF extends DebugLog {

    protected static ObservableList<DebugEntry> debugLogTF = FXCollections.observableList(new ArrayList<>());

    public static void log(String s) {
        debugLogTF.clear();
        debugLogTF.add(new DebugEntry(s));
    }

    public static ObservableList<DebugEntry> getDebugLogTF() {
        return debugLogTF;
    }
}
