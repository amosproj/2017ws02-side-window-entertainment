package de.tuberlin.amos.ws17.swit.common;

import java.util.Properties;

public class ApiConfig {

    private Properties configFile;

    private static ApiConfig instance;

    private ApiConfig() {
        configFile = new java.util.Properties();
        try {
            configFile.load(this.getClass().getClassLoader().getResourceAsStream("api-config.cfg"));
        } catch (Exception eta) {
            eta.printStackTrace();
        }
    }

    private String getValue(String key) {
        return configFile.getProperty(key);
    }

    public static String getProperty(String key) {
        if (instance == null) instance = new ApiConfig();
        return instance.getValue(key);
    }

    public static String getCloudVisionKey() {
        return getProperty("CloudVision");
    }
}

