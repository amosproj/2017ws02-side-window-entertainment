package de.tuberlin.amos.ws17.swit.application;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TransformInterpolationTest {

  private static Transform identityTransform;
  private static Transform translation;
  private static Transform yawRotation;

  @Before
  public void setUp() throws Exception {
    identityTransform = Transform.IDENTITY;
    translation = new Transform(Rotation.IDENTITY, new Vector3D(1.5, -2, 1));
    yawRotation = new Transform(new Rotation(Vector3D.PLUS_K, Math.PI / 2.0, RotationConvention.VECTOR_OPERATOR), Vector3D.ZERO);
  }

  @Test
  public void interpolateYawRotationFirst() {
    Transform i_0 = TransformInterpolation.interpolate(identityTransform, yawRotation, 0);
    double angle_0 = i_0.getRotation().getAngle();

    assertEquals(i_0.getTranslation().getX(), 0.0, 0.0);
    assertEquals(i_0.getTranslation().getY(), 0.0, 0.0);
    assertEquals(i_0.getTranslation().getZ(), 0.0, 0.0);
    assertEquals(angle_0, 0.0, 0.0);
  }

  @Test
  public void interpolateYawRotationMean() {
    Transform i_05 = TransformInterpolation.interpolate(identityTransform, yawRotation, 0.5);
    double angle_05 = i_05.getRotation().getAngle();

    assertEquals(i_05.getTranslation().getX(), 0.0, 0.0);
    assertEquals(i_05.getTranslation().getY(), 0.0, 0.0);
    assertEquals(i_05.getTranslation().getZ(), 0.0, 0.0);
    assertEquals(angle_05, Math.PI / 4.0, 0.0);
  }

  @Test
  public void interpolateYawRotationSecond() {
    Transform i_1 = TransformInterpolation.interpolate(identityTransform, yawRotation, 1);
    double angle_1 = i_1.getRotation().getAngle();

    assertEquals(i_1.getTranslation().getX(), 0.0, 0.0);
    assertEquals(i_1.getTranslation().getY(), 0.0, 0.0);
    assertEquals(i_1.getTranslation().getZ(), 0.0, 0.0);
    assertEquals(angle_1, Math.PI / 2.0, 0.001);
  }

  @Test
  public void interpolateTranslationFirst() {
    Transform i_0 = TransformInterpolation.interpolate(identityTransform, translation, 0);
    double angle_0 = i_0.getRotation().getAngle();

    assertEquals(i_0.getTranslation().getX(), 0.0, 0.0);
    assertEquals(i_0.getTranslation().getY(), 0.0, 0.0);
    assertEquals(i_0.getTranslation().getZ(), 0.0, 0.0);
    assertEquals(angle_0, 0.0, 0.0);
  }

  @Test
  public void interpolateTranslationMean() {
    Transform i_05 = TransformInterpolation.interpolate(identityTransform, translation, 0.5);
    double angle_05 = i_05.getRotation().getAngle();

    assertEquals(i_05.getTranslation().getX(), 0.75, 0.001);
    assertEquals(i_05.getTranslation().getY(), -1, 0.001);
    assertEquals(i_05.getTranslation().getZ(), 0.5, 0.001);
    assertEquals(angle_05, 0.0, 0.0);
  }
}
