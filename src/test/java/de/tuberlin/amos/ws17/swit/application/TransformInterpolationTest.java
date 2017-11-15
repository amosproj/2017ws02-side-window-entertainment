package de.tuberlin.amos.ws17.swit.application;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TransformInterpolationTest {

  private static Transform identityTransform;
  private static Transform yawRotation;

  @Before
  public void setUp() throws Exception {
    identityTransform = Transform.IDENTITY;

    yawRotation = new Transform(new Rotation(Vector3D.PLUS_K, Math.PI / 2.0, RotationConvention.VECTOR_OPERATOR), Vector3D.ZERO);
  }

  @Test public void interpolateYawRotation() {
  }
}
