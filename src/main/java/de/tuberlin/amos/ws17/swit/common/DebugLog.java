package de.tuberlin.amos.ws17.swit.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class DebugLog {

    private static boolean showUserTracking = true;
    private static boolean showLandscapeTracking = true;
    private static boolean showPoi = true;
    private static boolean showInformationSource = true;
    private static boolean showImageAnalysis = true;
    private static boolean showGps = false;
    private static boolean showApplicationView = true;

    private static ObservableList<DebugEntry> debugLog = FXCollections.observableList(new ArrayList<>());

    private static ObservableList<DebugEntry> shownLog = FXCollections.observableList(new ArrayList<>());

    // adds entries to both lists. If 'both' is false, the code is only reused for filling the 'shownLog' list
    private static void addEntry(DebugEntry entry, boolean both){
        if (both) debugLog.add(entry);
        String src = entry.source;
        //System.out.println("src: " + src);
        if ((showUserTracking && (src.equals("JavoNetUserTracker") || src.equals("UserTrackerMock") ))
                || (showGps && (src.equals("GpsTrackerMock") || src.equals("GpsPortReader") || src.equals("GpsTrackerImplementation")))
                || (showImageAnalysis && src.equals("CloudVision"))
                || (showInformationSource && (src.equals("AbstractProvider") || src.equals("InformationProviderMock")))
                || (showLandscapeTracking && (src.equals("LandscapeTrackerMock") || src.equals("LandscapeTrackerImplementation")))
                // || showPoi && *No class uses DebugLog yet*
                || (showApplicationView && src.equals("ApplicationViewModelImplementation"))
                )
            shownLog.add(entry);
    }

    public static void log(Object o) { addEntry(new DebugEntry(o.toString()), true); }

    public static void log(String s) {
        addEntry(new DebugEntry(s), true);
    }

    public static void log(boolean b) {
        addEntry(new DebugEntry((b ? "true" : "false")), true);
    }

    public static void log(int i) {
        addEntry(new DebugEntry(Integer.toString(i)), true);
    }

    public static void log(long l) {
        addEntry(new DebugEntry(Long.toString(l)), true);
    }

    public static void log(double d) {
        addEntry(new DebugEntry(Double.toString(d)), true);
    }

    public static void log(float f) {
        addEntry(new DebugEntry(Float.toString(f)), true);
    }

    public static void log(char c) {
        addEntry(new DebugEntry(Character.toString(c)), true);
    }

    public static ObservableList<DebugEntry> getDebugLog() {
        //return debugLog;
        return shownLog;
    }

    public static void toggleModule(String module){
        // change booleans
        if (module == "UserTracking")   showUserTracking = !showUserTracking;
        if (module == "LandscapeTracking") showLandscapeTracking = !showLandscapeTracking;
        if (module == "POI") showPoi = !showPoi;
        if (module == "InformationSource") showInformationSource = !showInformationSource;
        if (module == "ImageAnalysis") showImageAnalysis = !showInformationSource;
        if (module == "GPS") showGps = !showGps;
        if (module == "ApplicationView") showApplicationView = !showApplicationView;

        System.out.println("toggleModule: " + module + " -> " + showGps);

        // clear shownLog and refill it with the selected values
        shownLog.clear();
        System.out.println("shownLog size: " + shownLog.size());
        for (int i = 0; i < debugLog.size(); i++){
            addEntry(debugLog.get(i), false);
        }

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
    }
}