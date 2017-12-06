package de.tuberlin.amos.ws17.swit.tracking.javonet;

import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.JavonetFramework;
import com.javonet.api.NObject;

import java.nio.file.Paths;

public class JavoNetService {

    private static boolean isInitialized = false;
    public static NObject dotNetUserTracker;
    public static NObject dotNetCameraService;

    public static void initialize() throws JavonetException {
        if (!isInitialized) {
            Javonet.activate("christian.fengler@campus.tu-berlin.de", "Hs52-Rz97-Bi4j-z5RF-Ee2d", JavonetFramework.v45);
            String path = Paths.get("libs/DotNetTracking.dll").toAbsolutePath().toString();
            Javonet.addReference(path);
            dotNetCameraService = Javonet.New("CameraService");
            dotNetUserTracker = Javonet.New("UserTracker");
            isInitialized = true;
        }
    }
}
