package de.tuberlin.amos.ws17.swit.tracking;

import de.tuberlin.amos.ws17.swit.tracking.camera.CameraService;

public interface UserTracker {

    boolean getIsUserTracked();
    UserHeadPose getUserHeadPose();
    UserExpressions getUserExpressions();

    boolean startTracking();
    boolean stopTracking();


}
