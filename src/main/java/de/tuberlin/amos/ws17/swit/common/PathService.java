package de.tuberlin.amos.ws17.swit.common;

import com.sun.javafx.PlatformUtil;
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
            if (PlatformUtil.isWindows()) {
                pathName = pathName.substring(1, pathName.lastIndexOf("/"));
            } else {
                pathName = pathName.substring(0, pathName.lastIndexOf("/"));
            }
            return pathName;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
