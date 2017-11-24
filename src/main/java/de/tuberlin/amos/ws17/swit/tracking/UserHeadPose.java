package de.tuberlin.amos.ws17.swit.tracking;

import de.tuberlin.amos.ws17.swit.common.Vector3D;

public class UserHeadPose {

    public boolean isTracked;

    public Vector3D centerOfHead;

    public float yaw;
    public float pitch;
    public float roll;

    public String toString() {
        String result = "[UserHeadPose]" + System.lineSeparator();
        result += "isTracked: " + Boolean.toString(isTracked) + System.lineSeparator();;

        if (isTracked)
        {
            result += "centerOfHead: " + centerOfHead.toString() + System.lineSeparator();;
            result += "yaw: " + Float.toString(yaw) + System.lineSeparator();;
            result += "pitch: " + Float.toString(pitch) + System.lineSeparator();;
            result += "roll: " + Float.toString(roll) + System.lineSeparator();;
        }

        return result;
    }
}
