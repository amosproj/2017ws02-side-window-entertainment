package de.tuberlin.amos.ws17.swit.tracking.camera;

public interface CameraService extends AutoCloseable {

    void loadCameras();
    void selectUserTrackingCamera() throws CameraNotFoundException;
    void selectLandscapeTrackingCamera() throws CameraNotFoundException;
    //Resolution_ColorDepth_FrameRateCombination getResolution_ColorDepth_FrameRateCombination(Camera camera);
}
