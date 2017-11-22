package de.tuberlin.amos.ws17.swit.common;

public class Point3D {
    public float x;
    public float y;
    public float z;

    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return "(" + Float.toString(x) + "," + Float.toString(y) + "," + Float.toString(z) + ")";
    }
}
