package de.tuberlin.amos.ws17.swit.tracking.camera;

import com.javonet.JavonetException;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.tracking.javonet.JavoNetService;
//Diese Klasse wrappt die Funktionalität, mit der die Konnektivität der Intel RealSense RS300
// auf .NET Seite geprüft wird.
//Durch aufruf der selectUserTrackingCamera, wird auf .NET-Seite die Kamera ausgewählt und
// steht dann für die Verwendung bereit.
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
                DebugLog.log(DebugLog.SOURCE_USERTRACKING,"Intel RealSenseSR300 found");
                throw new CameraNotFoundException();
            }
            DebugLog.log(DebugLog.SOURCE_USERTRACKING,"Intel RealSenseSR300 not found");
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
