package de.tuberlin.amos.ws17.swit.tracking.javonet;

import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.JavonetFramework;
import com.javonet.api.NObject;
import de.tuberlin.amos.ws17.swit.common.ApiConfig;
import de.tuberlin.amos.ws17.swit.common.DebugLog;

import java.nio.file.Paths;

public class JavoNetService {

    private static boolean isInitialized = false;
    public static NObject dotNetUserTracker;
    public static NObject dotNetCameraService;

    public static void initialize() throws JavonetException {
        if (!isInitialized) {
            //DebugLog.log("JavoNetService initialize in");
            Javonet.activate(ApiConfig.getProperty("JavoNetEmail"), ApiConfig.getProperty("JavoNetLicenceKey"), JavonetFramework.v45);
            String path = Paths.get("libs/DotNetTracking.dll").toAbsolutePath().toString();
            Javonet.addReference(path);
            dotNetCameraService = Javonet.New("CameraService");
            dotNetUserTracker = Javonet.New("UserTracker");
            isInitialized = true;
            //DebugLog.log("JavoNetService initialize out");
        }
    }
}
