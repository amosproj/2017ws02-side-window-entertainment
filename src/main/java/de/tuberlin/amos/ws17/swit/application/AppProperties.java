package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.common.PathService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppProperties extends Properties {
    private static Logger LOGGER = Logger.getLogger(AppProperties.class.getName());
    private static AppProperties instance;

    private static String appPropertiesFilename = "app.properties";

    public boolean usePoiService;
    public boolean useKnowledgeGraph;
    public boolean useWikipedia;
    public boolean useExternalCamera;
    public boolean useDemoVideo;
    public boolean useIntelRealSense;
    public boolean useCloudVision;
    public boolean useGpsModule;
    public boolean useDebugLog;
    public boolean useFullscreen;
    public boolean useFullscreenWithoutWindowChrome;
    public int mapsPoisLoadDistance;
    public boolean useTensorflow;
    public boolean useAnimations;

    public static AppProperties getInstance() {
        if (instance == null) {
            String pathOfRunningJar = PathService.getPathOfRunningJar();
            String filePath = Paths.get(pathOfRunningJar, appPropertiesFilename).toString();

            try {
                instance = new AppProperties(filePath);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not load app.properties from file check " + filePath);
                instance = new AppProperties();
            }
        }
        return instance;
    }

    private AppProperties() {
        useGpsModule = false;
        useIntelRealSense = false;
        useExternalCamera = false;
        useDemoVideo = false;
        useCloudVision = false;
        useWikipedia = false;
        useKnowledgeGraph = false;
        usePoiService = false;
        useDebugLog = true;
        useFullscreen = true;
        useFullscreenWithoutWindowChrome = false;
        mapsPoisLoadDistance = 300;
        useAnimations = false;
    }

    private AppProperties(String propertyFilePath) throws IOException {
        InputStream input = new FileInputStream(propertyFilePath);
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
        useFullscreen = get("fullscreen").equals("1");
        useFullscreenWithoutWindowChrome = get("fullscreen").equals("2");
        useTensorflow = get("tensorflow").equals("1");
        mapsPoisLoadDistance = Integer.parseInt(get("mapsPoisLoadDistance").toString());
        useAnimations = get("animations").equals("1");
        System.out.println();
    }
}
