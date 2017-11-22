package de.tuberlin.amos.ws17.swit.tracking.camera;

import de.tuberlin.amos.ws17.swit.tracking.camera.Camera;

public interface CameraService {

    void loadCameras();
    Camera GetUserTrackingCamera();
    Camera GetLandscapeTrackingCamera();
    Resolution_ColorDepth_FrameRateCombination getResolution_ColorDepth_FrameRateCombination(Camera camera);
}
