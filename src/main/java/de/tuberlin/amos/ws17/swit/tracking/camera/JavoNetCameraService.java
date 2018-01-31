package de.tuberlin.amos.ws17.swit.tracking.camera;

import com.javonet.JavonetException;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.tracking.javonet.JavoNetService;

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
                DebugLog.log("JavoNetCameraService Intel RealSenseSR300 found");
                throw new CameraNotFoundException();
            }
            DebugLog.log("JavoNetCameraService Intel RealSenseSR300 not found");
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
