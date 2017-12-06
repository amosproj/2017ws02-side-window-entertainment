package de.tuberlin.amos.ws17.swit.tracking.camera;

import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.api.NObject;
import de.tuberlin.amos.ws17.swit.tracking.javonet.JavoNetService;

import java.nio.file.Paths;

public class JavoNetCameraService implements CameraService {



    public JavoNetCameraService() throws JavonetException {
        JavoNetService.initialize();
    }

    @Override
    public void loadCameras() {
        try {
            JavoNetService.dotNetCameraService.invoke("LoadCameras");
        } catch (JavonetException e) {
            e.printStackTrace();
        }
    }

    public void selectUserTrackingCamera() throws CameraNotFoundException {
        try {
            boolean result = JavoNetService.dotNetCameraService.invoke("SelectIntelRealSenseSR300");

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
