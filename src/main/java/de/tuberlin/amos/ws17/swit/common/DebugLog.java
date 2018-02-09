package de.tuberlin.amos.ws17.swit.common;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.apache.jena.base.Sys;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;

public class DebugLog {

    private static boolean showUserTracking = true;
    private static boolean showLandscapeTracking = true;
    private static boolean showPoi = true;
    private static boolean showInformationSource = true;
    private static boolean showImageAnalysis = true;
    private static boolean showGps = false;
    private static boolean showApplicationView = true;

    private static ObservableList<DebugEntry> debugLog = null;
    private static FilteredList<DebugEntry> debugLogFiltered = null;

    static {
        debugLog = FXCollections.observableArrayList();
        debugLogFiltered = new FilteredList<>(debugLog);
    }

    public static void log(String source, String s) {
        Platform.runLater(() -> {
            debugLog.add(new DebugEntry(source, s));
        });
    }

    public static ObservableList<DebugEntry> getDebugLog() {
        //return debugLog;
        return debugLogFiltered;
    }

    public static final String SOURCE_USERTRACKING = "UserTracking";
    public static final String SOURCE_LANDSCAPETRACKING = "LandscapeTracking";
    //public static final String SOURCE_POI = "Poi";
    public static final String SOURCE_INFORMATIONSOURCE = "InformationSource";
    public static final String SOURCE_IMAGEANALYSIS = "ImageAnalysis";
    public static final String SOURCE_GPS = "GPS";
    public static final String SOURCE_VIEW = "View";

    public static void toggleModule(String module){
        if (module == "UserTracking")   showUserTracking = !showUserTracking;
        if (module == "LandscapeTracking") showLandscapeTracking = !showLandscapeTracking;
        if (module == "POI") showPoi = !showPoi;
        if (module == "InformationSource") showInformationSource = !showInformationSource;
        if (module == "ImageAnalysis") showImageAnalysis = !showImageAnalysis;
        if (module == "GPS") showGps = !showGps;
        if (module == "ApplicationView") showApplicationView = !showApplicationView;

        debugLogFiltered.setPredicate(debugEntry -> {
            if (debugEntry.source.equals(SOURCE_USERTRACKING)) {
                return showUserTracking;
            }
            else if (debugEntry.source.equals(SOURCE_GPS)) {
                return showGps;
            }
            else if (debugEntry.source.equals(SOURCE_IMAGEANALYSIS)) {
                return showImageAnalysis;
            }
            else if (debugEntry.source.equals(SOURCE_INFORMATIONSOURCE)) {
                return showInformationSource;
            }
            else if (debugEntry.source.equals(SOURCE_LANDSCAPETRACKING)) {
                return showLandscapeTracking;
            }
            else if (debugEntry.source.equals(SOURCE_VIEW)) {
                return showApplicationView;
            }
           return true;
        });
        System.out.println("toggleModule: " + module + " -> " + showGps);
    }

    public static class DebugEntry {
        private LocalDateTime timeStamp;
        private String source;
        private String message;

//        public DebugEntry(String message) {
//            timeStamp = new LocalDateTime();
//
//            String temp = sun.reflect.Reflection.getCallerClass(4).getName();
//            source = temp.substring(temp.lastIndexOf(".") + 1);
//            this.message = message;
//        }

        public DebugEntry(String source, String message) {
            timeStamp = new LocalDateTime();

            //String temp = sun.reflect.Reflection.getCallerClass(4).getName();
            this.source = source;//temp.substring(temp.lastIndexOf(".") + 1);
            this.message = message;
        }

        @Override
        public String toString() {
            return "[" + source + " " + timeStamp.toString("HH:mm:ss") + "]: " + message;
        }
    }

    public static boolean getModuleStatus(String module){
        if (module.equals("POI")) return showPoi;
        if (module.equals("GPS")) return showGps;
        if (module.equals("ApplicationView")) return showApplicationView;
        if (module.equals("InformationSource")) return showInformationSource;
        if (module.equals("LandscapeTracking")) return showLandscapeTracking;
        if (module.equals("UserTracking")) return showUserTracking;
        if (module.equals("ImageAnalysis")) return showImageAnalysis;
        return false;
    }
}
