package de.tuberlin.amos.ws17.swit.common;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.joda.time.LocalDateTime;

import java.util.Arrays;

public class DebugLog {

    public static final int userTracking      = 0;
    public static final int landscapeTracking = 1;
    public static final int poi               = 2;
    public static final int informationSource = 3;
    public static final int imageAnalysis     = 4;
    public static final int gps               = 5;
    public static final int applicationView   = 6;

    private static ObservableList<DebugEntry> debugLog;
    private static FilteredList<DebugEntry>   debugLogFiltered;
    private static boolean[] sourceFlags = new boolean[7];

    static {
        debugLog = FXCollections.observableArrayList();
        debugLogFiltered = new FilteredList<>(debugLog, s -> true);
        Arrays.fill(sourceFlags, true);
    }

    public static void log(String source, String message) {
        try {
            Platform.runLater(() -> debugLog.add(new DebugEntry(source, message)));
        }catch (IllegalStateException e){
            System.out.println("                       (Toolkit of DebugLog not initialized, log message:)\n"+
            message);
        }
    }

    public static ObservableList<DebugEntry> getDebugLog() {
        return debugLogFiltered;
    }

    public static final String SOURCE_USERTRACKING      = "UserTracking";
    public static final String SOURCE_LANDSCAPETRACKING = "LandscapeTracking";
    public static final String SOURCE_MAPS_POI          = "Poi";
    public static final String SOURCE_INFORMATIONSOURCE = "InformationSource";
    public static final String SOURCE_IMAGEANALYSIS     = "ImageAnalysis";
    public static final String SOURCE_GPS               = "GPS";
    public static final String SOURCE_VIEW              = "View";


    public static void toggleModule(int module) {
        sourceFlags[module] = !sourceFlags[module];
        applyFilter();
    }

    private static void applyFilter() {
        // reset filter
        debugLogFiltered.setPredicate(s -> true);
        // apply filter using boolean array
        debugLogFiltered.setPredicate(debugEntry -> {
            int currentModule = -1;
            switch (debugEntry.source) {
                case SOURCE_USERTRACKING:
                    currentModule = userTracking;
                    break;
                case SOURCE_GPS:
                    currentModule = gps;
                    break;
                case SOURCE_IMAGEANALYSIS:
                    currentModule = imageAnalysis;
                    break;
                case SOURCE_INFORMATIONSOURCE:
                    currentModule = informationSource;
                    break;
                case SOURCE_LANDSCAPETRACKING:
                    currentModule = landscapeTracking;
                    break;
                case SOURCE_VIEW:
                    currentModule = applicationView;
                    break;
                case SOURCE_MAPS_POI:
                    currentModule = poi;
                    break;
            }
            return currentModule >= 0 && sourceFlags[currentModule];
        });
    }

    public static class DebugEntry {
        private LocalDateTime timeStamp;
        private String        source;
        private String        message;

        DebugEntry(String tag, String message) {
            timeStamp = new LocalDateTime();
            this.source = tag;
            this.message = message;
        }

        @Override
        public String toString() {
            return "[" + source + " " + timeStamp.toString("HH:mm:ss") + "]: " + message;
        }
    }

    public static boolean getModuleStatus(int module) {
        return sourceFlags[module];
    }
}
