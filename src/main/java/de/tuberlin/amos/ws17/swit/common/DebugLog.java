package de.tuberlin.amos.ws17.swit.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;

public class DebugLog {

    private static ObservableList<DebugEntry> debugLog;

    public static void initDebugLog() {
        debugLog = FXCollections.observableList(new ArrayList<>());
    }

    public static void log(Object o) {
        debugLog.add(new DebugEntry(o.toString()));
    }

    public static void log(String s) {
        debugLog.add(new DebugEntry(s));
    }

    public static void log(boolean b) {
        debugLog.add(new DebugEntry((b ? "true" : "false")));
    }

    public static void log(int i) {
        debugLog.add(new DebugEntry(Integer.toString(i)));
    }

    public static void log(long l) {
        debugLog.add(new DebugEntry(Long.toString(l)));
    }

    public static void log(double d) {
        debugLog.add(new DebugEntry(Double.toString(d)));
    }

    public static void log(float f) {
        debugLog.add(new DebugEntry(Float.toString(f)));
    }

    public static void log(char c) {
        debugLog.add(new DebugEntry(Character.toString(c)));
    }

    public static ObservableList<DebugEntry> getDebugLog() {
        return debugLog;
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
            source = sun.reflect.Reflection.getCallerClass(1).getName();
            this.message = message;
        }

        @Override
        public String toString() {
            return "[" + source + "," + timeStamp.toString() + "]: " + message;
        }
    }
}