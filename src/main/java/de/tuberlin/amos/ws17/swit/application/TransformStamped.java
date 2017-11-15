package de.tuberlin.amos.ws17.swit.application;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class TransformStamped extends Transform {
  public TransformStamped(long stamp, Rotation r, Vector3D t) {
    super(r, t);
  }

  public TransformStamped(long stamp, Transform t) {
    super(t.getRotation(), t.getTranslation());
  }

  private long stamp;

  public  long getStamp() {
    return stamp;
  }
}
