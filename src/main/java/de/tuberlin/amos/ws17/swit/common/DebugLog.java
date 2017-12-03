package de.tuberlin.amos.ws17.swit.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;

public class DebugLog {
    public static ObservableList<String> debugLog = FXCollections.observableList(new ArrayList<>());
    public static void log(Object o, Object modul) {
        LocalDateTime timeStamp = new LocalDateTime();
        String moduleName =  modul.getClass().getName();
        String simpleName = moduleName.substring(moduleName.lastIndexOf('.') + 1);
        debugLog.add("[" + timeStamp + "] " + simpleName + " : " + o.toString());


    }
}
