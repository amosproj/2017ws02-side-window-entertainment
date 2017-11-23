package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.transform.TransformStamped;
import de.tuberlin.amos.ws17.swit.transform.TransformationBuffer;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class TransformationBufferDemo {
  public static void main(String[] args){
    TransformationBuffer tfb = new TransformationBuffer();

    System.out.println("Insert transform data for some timestamps:");

    TransformStamped t1 = new TransformStamped(1, Rotation.IDENTITY, Vector3D.ZERO);

    tfb.insert(t1);

    System.out.format("--- Inserted transform (stamp=%d): [q=%f, x=%f, y=%f, z=%f], [x=%f, y=%f, z=%f]%n", t1.getStamp(),
        t1.getRotation().getQ0(), t1.getRotation().getQ1(), t1.getRotation().getQ2(), t1.getRotation().getQ3(),
        t1.getTranslation().getX(), t1.getTranslation().getY(), t1.getTranslation().getZ());

    TransformStamped t2 = new TransformStamped(3, new Rotation(Vector3D.PLUS_K, Math.PI / 2.0, RotationConvention.VECTOR_OPERATOR), new Vector3D(1, 0, 2));

    tfb.insert(t2);

    System.out.format("--- Inserted transform (stamp=%d): [q=%f, x=%f, y=%f, z=%f], [x=%f, y=%f, z=%f]%n", t2.getStamp(),
        t2.getRotation().getQ0(), t2.getRotation().getQ1(), t2.getRotation().getQ2(), t2.getRotation().getQ3(),
        t2.getTranslation().getX(), t2.getTranslation().getY(), t2.getTranslation().getZ());

    System.out.println("Now lookup arbitrary timestamps and get interpolated results:");

    TransformStamped response = tfb.lookup(1);

    System.out.format("--- Lookup result (stamp=%d): [q=%f, x=%f, y=%f, z=%f], [x=%f, y=%f, z=%f]%n", response.getStamp(),
        response.getRotation().getQ0(), response.getRotation().getQ1(), response.getRotation().getQ2(), response.getRotation().getQ3(),
        response.getTranslation().getX(), response.getTranslation().getY(), response.getTranslation().getZ());

    response = tfb.lookup(2);

    System.out.format("--- Lookup result (stamp=%d): [q=%f, x=%f, y=%f, z=%f], [x=%f, y=%f, z=%f]%n", response.getStamp(),
        response.getRotation().getQ0(), response.getRotation().getQ1(), response.getRotation().getQ2(), response.getRotation().getQ3(),
        response.getTranslation().getX(), response.getTranslation().getY(), response.getTranslation().getZ());

    response = tfb.lookup(3);

    System.out.format("--- Lookup result (stamp=%d): [q=%f, x=%f, y=%f, z=%f], [x=%f, y=%f, z=%f]%n", response.getStamp(),
        response.getRotation().getQ0(), response.getRotation().getQ1(), response.getRotation().getQ2(), response.getRotation().getQ3(),
        response.getTranslation().getX(), response.getTranslation().getY(), response.getTranslation().getZ());
  }
}
