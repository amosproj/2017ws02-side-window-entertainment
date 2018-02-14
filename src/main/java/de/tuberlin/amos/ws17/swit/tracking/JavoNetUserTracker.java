package de.tuberlin.amos.ws17.swit.tracking;

import com.javonet.JavonetException;
import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.UserExpressions;
import de.tuberlin.amos.ws17.swit.common.UserPosition;
import de.tuberlin.amos.ws17.swit.common.Vector3D;
import de.tuberlin.amos.ws17.swit.tracking.camera.CameraService;
import de.tuberlin.amos.ws17.swit.tracking.camera.JavoNetCameraService;
import de.tuberlin.amos.ws17.swit.tracking.javonet.JavoNetService;

//Diese Klasse stellt die User-Tracking Funktionalität für Java bereit.
//Sie ist ein Wrapper der .NET Assemblies mit Hilfe von JavoNet aufruft.
//Als Hardware wird die IntelRealSense SR300 vorausgesetzt.
//Dafür muss in der app.properties die Einstellung usercamera=1 gesetzt sein.
public class JavoNetUserTracker implements UserTracker {

    private CameraService cameraService;

    public JavoNetUserTracker() {
        try {
            JavoNetService.initialize();
            cameraService = new JavoNetCameraService();
        } catch (JavonetException e) {
            e.printStackTrace();
        }
    }

    public boolean isHardwareAvailable() {
        try {
            boolean result = JavoNetService.dotNetUserTracker.get("IsHardwareAvailable");
            return result;
        } catch (JavonetException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean isUserTracked() {
        try {
            boolean result = JavoNetService.dotNetUserTracker.get("IsUserTracked");
            return result;
        } catch (JavonetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public UserPosition getUserPosition() {
        UserPosition result = null;
        if (isUserTracked()) {
            try {
                float userHeadPositionX = JavoNetService.dotNetUserTracker.get("UserHeadPositionX");
                float userHeadPositionY = JavoNetService.dotNetUserTracker.get("UserHeadPositionY");
                float userHeadPositionZ = JavoNetService.dotNetUserTracker.get("UserHeadPositionZ");

                float userHeadPositionYaw = JavoNetService.dotNetUserTracker.get("UserHeadPositionYaw");
                float userHeadPositionPitch = JavoNetService.dotNetUserTracker.get("UserHeadPositionPitch");
                float userHeadPositionRoll = JavoNetService.dotNetUserTracker.get("UserHeadPositionRoll");

                result = new UserPosition(
                        new Vector3D(userHeadPositionX, userHeadPositionY, userHeadPositionZ),
                        new Vector3D(userHeadPositionRoll, userHeadPositionPitch, userHeadPositionYaw)
                );
            } catch (JavonetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public UserExpressions getUserExpressions() {
        UserExpressions result = null;
        if (isUserTracked()) {
            try {
                boolean isKiss = JavoNetService.dotNetUserTracker.get("UserExpressionKiss");
                boolean isToungueOut = JavoNetService.dotNetUserTracker.get("UserExpressionTongueOut");
                boolean isSmile = JavoNetService.dotNetUserTracker.get("UserExpressionSmile");
                boolean isMouthOpen = JavoNetService.dotNetUserTracker.get("UserExpressionMouthOpen");
                result =  new UserExpressions();
                result.setKiss(isKiss);
                result.setTongueOut(isToungueOut);
                result.setSmile(isSmile);
                result.setMouthOpen(isMouthOpen);
            } catch (JavonetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean startTracking() {
        try {
            JavoNetService.dotNetUserTracker.invoke("StartTracking");
        } catch (JavonetException e) {
            DebugLog.log(DebugLog.SOURCE_USERTRACKING,"Tracking Thread not started");
            return false;
        }
        DebugLog.log(DebugLog.SOURCE_USERTRACKING,"Tracking Thread started");
        return true;
    }

    @Override
    public boolean stopTracking() {
        try {
            JavoNetService.dotNetUserTracker.invoke("StopTracking");
        } catch (JavonetException e) {
            DebugLog.log(DebugLog.SOURCE_USERTRACKING,"Tracking Thread not stopped");
            return false;
        }
        DebugLog.log(DebugLog.SOURCE_USERTRACKING,"Tracking Thread stopped");
        return true;
    }
}
