package de.tuberlin.amos.ws17.swit.application;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Transform {
  public static final Transform IDENTITY = new Transform(Rotation.IDENTITY, Vector3D.ZERO);

  public Transform(Rotation r, Vector3D t) {
    this.rotation = r;
    this.translation = t;
  }

  private Rotation rotation;
  private Vector3D translation;

  public Rotation getRotation() {
    return this.rotation;
  }

  public Vector3D getTranslation() {
    return this.translation;
  }
}
