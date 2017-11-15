package de.tuberlin.amos.ws17.swit.application;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

public class TransformInterpolation {
  /**
   * https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Quaternion.java
   * <code>slerp</code> sets this quaternion's value as an interpolation
   * between two other quaternions.
   *
   * @param q1 the first quaternion.
   * @param q2 the second quaternion.
   * @param t  the amount to interpolate between the two quaternions.
   */
  private static Rotation slerp(Rotation q1, Rotation q2, double t) {
    // Create a local quaternion to store the interpolated quaternion
    if (q1.getQ1() == q2.getQ1() && q1.getQ2() == q2.getQ2() && q1.getQ3() == q2.getQ3() && q1.getQ0() == q2.getQ0()) {
      return q1;
    }

    double result = (q1.getQ1() * q2.getQ1()) + (q1.getQ2() * q2.getQ2()) + (q1.getQ3() * q2.getQ3())
        + (q1.getQ0() * q2.getQ0());

    if (result < 0.0f) {
      // Negate the second quaternion and the result of the dot product
      q2 = new Rotation(-q2.getQ0(), -q2.getQ1(), -q2.getQ2(), -q2.getQ3(), false);
      result = -result;
    }

    // Set the first and second scale for the interpolation
    double scale0 = 1 - t;
    double scale1 = t;

    // Check if the angle between the 2 quaternions was big enough to
    // warrant such calculations
    if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
      // and then store the sin() of that angle
      double theta = FastMath.acos(result);
      double invSinTheta = 1f / FastMath.sin(theta);

      // Calculate the scale for q1 and q2, according to the angle and
      // it's sine value
      scale0 = FastMath.sin((1 - t) * theta) * invSinTheta;
      scale1 = FastMath.sin((t * theta)) * invSinTheta;
    }

    // Calculate the x, y, z and w values for the quaternion by using a
    // special
    // form of linear interpolation for quaternions.
    double x = (scale0 * q1.getQ1()) + (scale1 * q2.getQ1());
    double y = (scale0 * q1.getQ2()) + (scale1 * q2.getQ2());
    double z = (scale0 * q1.getQ3()) + (scale1 * q2.getQ3());
    double w = (scale0 * q1.getQ0()) + (scale1 * q2.getQ0());

    // Return the interpolated quaternion
    return new Rotation(w, x, y, z, true);
  }

  private static Vector3D interpolate(Vector3D v1, Vector3D v2, double t) {
    return new Vector3D(1 - t, v1, t, v2);
  }

  public static Transform interpolate(Transform t1, Transform t2, double t) {
    return new Transform(slerp(t1.getRotation(), t2.getRotation(), t), interpolate(t1.getTranslation(), t2.getTranslation(), t));
  }
}
