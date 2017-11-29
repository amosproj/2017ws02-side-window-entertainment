package de.tuberlin.amos.ws17.swit.tracking.camera;

import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.JavonetFramework;
import com.javonet.api.NAssembly;
import com.javonet.api.NObject;
import com.javonet.api.NType;

public class JavoNetCameraService implements CameraService {

    private NObject dotNetCameraService;

    public JavoNetCameraService() throws JavonetException {
        Javonet.addReference("C:/Users/CFengler/Documents/Visual Studio 2017/Projects/Cf.Test.RealSense/DotNetTracking/bin/Release/DotNetTracking.dll");
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
