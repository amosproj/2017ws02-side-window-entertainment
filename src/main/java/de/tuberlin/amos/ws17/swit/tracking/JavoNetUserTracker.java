package de.tuberlin.amos.ws17.swit.tracking;

import com.javonet.Javonet;
import com.javonet.JavonetException;
import com.javonet.JavonetFramework;
import com.javonet.api.NObject;
import de.tuberlin.amos.ws17.swit.common.UserExpressions;
import de.tuberlin.amos.ws17.swit.common.UserPosition;
import de.tuberlin.amos.ws17.swit.common.Vector3D;
import de.tuberlin.amos.ws17.swit.tracking.camera.CameraService;
import de.tuberlin.amos.ws17.swit.tracking.camera.JavoNetCameraService;

public class JavoNetUserTracker implements UserTracker {

    private NObject dotNetUserTracker;
    private CameraService cameraService;

    public JavoNetUserTracker() {
        try {
            Javonet.activate("christian.fengler@campus.tu-berlin.de", "Hs52-Rz97-Bi4j-z5RF-Ee2d", JavonetFramework.v45);
            cameraService = new JavoNetCameraService();
            dotNetUserTracker = Javonet.New("UserTracker");
        } catch (JavonetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getIsUserTracked() {
        try {
            boolean result = dotNetUserTracker.get("IsUserTracked");
            return result;
        } catch (JavonetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public UserPosition getUserPosition() {
        UserPosition result = null;
        if (getIsUserTracked()) {
            try {
                float userHeadPositionX = dotNetUserTracker.get("UserHeadPositionX");
                float userHeadPositionY = dotNetUserTracker.get("UserHeadPositionY");
                float userHeadPositionZ = dotNetUserTracker.get("UserHeadPositionZ");

                float userHeadPositionYaw = dotNetUserTracker.get("UserHeadPositionYaw");
                float userHeadPositionPitch = dotNetUserTracker.get("UserHeadPositionPitch");
                float userHeadPositionRoll = dotNetUserTracker.get("UserHeadPositionRoll");

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
        UserExpressions result = new UserExpressions();
        try {
            boolean isKiss = dotNetUserTracker.get("UserExpressionKiss");
            boolean isToungueOut = dotNetUserTracker.get("UserExpressionTongueOut");
            boolean isSmile = dotNetUserTracker.get("UserExpressionSmile");
            boolean isMouthOpen = dotNetUserTracker.get("UserExpressionMouthOpen");
            result.setKiss(isKiss);
            result.setTongueOut(isToungueOut);
            result.setSmile(isSmile);
            result.setMouthOpen(isMouthOpen);
        }catch (JavonetException e) {
            return null;
        }
        return result;
    }

    @Override
    public boolean startTracking() {
        try {
            dotNetUserTracker.invoke("StartTracking");
        } catch (JavonetException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean stopTracking() {
        try {
            dotNetUserTracker.invoke("StopTracking");
        } catch (JavonetException e) {
            return false;
        }
        return true;
    }
}
