package de.tuberlin.amos.ws17.swit.common;

public class UserPosition {
    private Vector3D headCenterPosition;
    private Vector3D lineOfSight; //roll = x, pitch = y, yaw = z

    public Vector3D getHeadCenterPosition() {
        return headCenterPosition;
    }

    //public void setHeadCenterPosition(Vector3D headCenterPosition) {
        //this.headCenterPosition = headCenterPosition;
    //}

    public Vector3D getLineOfSight() {
        return lineOfSight;
    }

    //public void setLineOfSight(Vector3D lineOfSight) {
    //    this.lineOfSight = lineOfSight;
    //}

    public UserPosition(Vector3D headCenterPosition, Vector3D lineOfSight) {
        this.headCenterPosition = headCenterPosition;
        this.lineOfSight = lineOfSight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPosition that = (UserPosition) o;

        if (headCenterPosition != null ? !headCenterPosition.equals(that.headCenterPosition) : that.headCenterPosition != null)
            return false;
        return lineOfSight != null ? lineOfSight.equals(that.lineOfSight) : that.lineOfSight == null;
    }

    @Override
    public String toString() {
        return "headCenterPosition: " + headCenterPosition.toString() + System.lineSeparator() +
                "lineOfSight: " + lineOfSight.toString();
    }
}
