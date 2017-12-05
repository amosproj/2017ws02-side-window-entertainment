package de.tuberlin.amos.ws17.swit.tracking.camera;

import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.JavonetFramework;
import com.javonet.api.NAssembly;
import com.javonet.api.NObject;
import com.javonet.api.NType;
import org.apache.jena.base.Sys;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavoNetCameraService implements CameraService {

    private NObject dotNetCameraService;

    public JavoNetCameraService() throws JavonetException {
        String path = Paths.get("libs/DotNetTracking.dll").toAbsolutePath().toString();
        Javonet.addReference(path);
        dotNetCameraService = Javonet.New("CameraService");
    }

    @Override
    public void loadCameras() {
        try {
            dotNetCameraService.invoke("LoadCameras");
        } catch (JavonetException e) {
            e.printStackTrace();
        }
    }

    public void selectUserTrackingCamera() throws CameraNotFoundException {
        try {
            boolean result = dotNetCameraService.invoke("SelectIntelRealSenseSR300");

            if (!result) {
                throw new CameraNotFoundException();
            }
        } catch (JavonetException e) {
            e.printStackTrace();
        }
    }

    public void selectLandscapeTrackingCamera() throws CameraNotFoundException {
        throw new CameraNotFoundException();
    }

    @Override
    public void close(){

    }
}
