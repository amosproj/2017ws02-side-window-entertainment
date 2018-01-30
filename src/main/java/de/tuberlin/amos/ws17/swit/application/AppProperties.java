package de.tuberlin.amos.ws17.swit.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppProperties extends Properties {
    private static Logger LOGGER = Logger.getLogger(AppProperties.class.getName());
    private static AppProperties instance;

    public boolean usePoiService;
    public boolean useKnowledgeGraph;
    public boolean useWikipedia;
    public boolean useExternalCamera;
    public boolean useDemoVideo;
    public boolean useIntelRealSense;
    public boolean useCloudVision;
    public boolean useGpsModule;
    public boolean useDebugLog;

    private AppProperties() {}

    public static AppProperties getInstance() {
        if (instance == null) {
            try {
                instance = new AppProperties("app.properties");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not load app.properties!");
            }
        }
        return instance;
    }

    private AppProperties(String propertyFilePath) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(propertyFilePath);
        load(input);
        initProperties();
    }

    private void initProperties() {
        useGpsModule = get("gpsmodule").equals("1");
        useIntelRealSense = get("usercamera").equals("1");
        useExternalCamera = get("camera").equals("1");
        useDemoVideo = get("camera").equals("2");
        useCloudVision = get("image_analysis").equals("1");
        useWikipedia = get("information_source").equals("1");
        useKnowledgeGraph = get("information_source").equals("1");
        usePoiService = get("poi_analysis").equals("1");
        useDebugLog = get("debuglog").equals("1");
    }
}
