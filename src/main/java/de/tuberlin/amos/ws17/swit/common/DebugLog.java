package de.tuberlin.amos.ws17.swit.common;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

    public static void log(String s) {
        Platform.runLater(() -> {
            debugLog.add(new DebugEntry(s));
        });
    }

    public static ObservableList<DebugEntry> getDebugLog() {
        //return debugLog;
        return debugLog;
    }

    public static void toggleModule(String module){
        if (module == "UserTracking")   showUserTracking = !showUserTracking;
        if (module == "LandscapeTracking") showLandscapeTracking = !showLandscapeTracking;
        if (module == "POI") showPoi = !showPoi;
        if (module == "InformationSource") showInformationSource = !showInformationSource;
        if (module == "ImageAnalysis") showImageAnalysis = !showInformationSource;
        if (module == "GPS") showGps = !showGps;
        if (module == "ApplicationView") showApplicationView = !showApplicationView;


            debugLogFiltered.setPredicate(debugEntry ->
                (showUserTracking && (debugEntry.source.equals("JavoNetUserTracker") || debugEntry.source.equals("UserTrackerMock")))
                    || (showGps && (debugEntry.source.equals("GpsTrackerMock") || debugEntry.source.equals("GpsPortReader") || debugEntry.source.equals("GpsTrackerImplementation")))
                    || (showImageAnalysis && debugEntry.source.equals("CloudVision"))
                    || (showInformationSource && (debugEntry.source.equals("AbstractProvider") || debugEntry.source.equals("InformationProviderMock")))
                    || (showLandscapeTracking && (debugEntry.source.equals("LandscapeTrackerMock") || debugEntry.source.equals("LandscapeTrackerImplementation")))
                    // || showPoi && *No class uses DebugLog yet*
                    || (showApplicationView && debugEntry.source.equals("ApplicationViewModelImplementation")));
        //});

        System.out.println("toggleModule: " + module + " -> " + showGps);
    }

    public static void setDebugLog(ObservableList<DebugEntry> debugLog) {
        DebugLog.debugLog = debugLog;
    }

    public static class DebugEntry {
        private LocalDateTime timeStamp;
        private String source;
        private String message;

        public DebugEntry(String message) {
            timeStamp = new LocalDateTime();
            String temp = sun.reflect.Reflection.getCallerClass(3).getName();
            source = temp.substring(temp.lastIndexOf(".") + 1);
            this.message = message;
        }

        @Override
        public String toString() {
            return "[" + source + " " + timeStamp.toString("HH:mm:ss") + "]: " + message;
        }

        public String getMessage() {
            return message;
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
