package de.tuberlin.amos.ws17.swit.common;

import de.tuberlin.amos.ws17.swit.application.view.ApplicationViewImplementation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PathService {

    public PathService() {
    }

    public static String getPathOfRunningJar() {
        try {

            String pathName = ApplicationViewImplementation.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            URLDecoder.decode(pathName,"utf-8");

            if (!System.getProperty("os.name").contains("Linux")) {
                pathName = pathName.substring(1,pathName.lastIndexOf("/") );
            }

            return pathName;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
