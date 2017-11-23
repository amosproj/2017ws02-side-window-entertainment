package de.tuberlin.amos.ws17.swit.demo;



import de.tuberlin.amos.ws17.swit.transform.TransformStamped;
import de.tuberlin.amos.ws17.swit.transform.TransformationBuffer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class TransformationExtrapolationDemo extends Application {
  @Override public void start(Stage stage) {
    TransformationBuffer tfb = new TransformationBuffer();
    TransformStamped t1 = new TransformStamped(0, Rotation.IDENTITY, Vector3D.ZERO);
    tfb.insert(t1);
    TransformStamped t2 = new TransformStamped(1000, new Rotation(Vector3D.PLUS_K, Math.PI / 2.0, RotationConvention.VECTOR_OPERATOR), new Vector3D(1, 0, 2));
    tfb.insert(t2);
    TransformStamped t3 = new TransformStamped(1500, new Rotation(Vector3D.PLUS_K, Math.PI / 2.0, RotationConvention.VECTOR_OPERATOR), new Vector3D(1.2, 0, 2));
    tfb.insert(t3);

    FlowPane root = new FlowPane();

    stage.setTitle("Transformation Intra-/Extrapolation Demo");
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    final ScatterChart<Number,Number> sc = new
        ScatterChart<Number,Number>(xAxis,yAxis);
    xAxis.setLabel("Time [sec]");
    yAxis.setLabel("Value");
    sc.setTitle("X Translation");

    XYChart.Series seriesInputData = new XYChart.Series();
    seriesInputData.setName("Input Data");
    seriesInputData.getData().add(new XYChart.Data(t1.getStamp() / 1000.0, t1.getTranslation().getX()));
    seriesInputData.getData().add(new XYChart.Data(t2.getStamp() / 1000.0, t2.getTranslation().getX()));
    seriesInputData.getData().add(new XYChart.Data(t3.getStamp() / 1000.0, t3.getTranslation().getX()));

    XYChart.Series seriesInterpolation = new XYChart.Series();
    seriesInterpolation.setName("Interpolated Lookups");

    TransformStamped response;

    for (long i = 100; i < 1000; i+=100) {
      response = tfb.lookup(i);
      seriesInterpolation.getData().add(new XYChart.Data(response.getStamp() / 1000.0, response.getTranslation().getX()));
    }

    for (long i = 1100; i < 1500; i+=100) {
      response = tfb.lookup(i);
      seriesInterpolation.getData().add(new XYChart.Data(response.getStamp() / 1000.0, response.getTranslation().getX()));
    }

    XYChart.Series seriesExtrapolation = new XYChart.Series();
    seriesExtrapolation.setName("Extrapolated Lookups");

    for (long i = 1600; i < 2200; i+=100) {
      response = tfb.lookup(i);
      seriesExtrapolation.getData().add(new XYChart.Data(response.getStamp() / 1000.0, response.getTranslation().getX()));
    }

    sc.getData().addAll(seriesInputData, seriesInterpolation, seriesExtrapolation);

    //-----------------------------------------------------------------------------------------------
    final NumberAxis xAxis2 = new NumberAxis();
    final NumberAxis yAxis2 = new NumberAxis();
    final ScatterChart<Number,Number> sc2 = new
        ScatterChart<Number,Number>(xAxis2,yAxis2);
    xAxis2.setLabel("Time [sec]");
    yAxis2.setLabel("Value");
    sc2.setTitle("Rotation around Z");

    XYChart.Series seriesInputData2 = new XYChart.Series();
    seriesInputData2.setName("Input Data");
    seriesInputData2.getData().add(new XYChart.Data(t1.getStamp() / 1000.0, t1.getRotation().getAngle()));
    seriesInputData2.getData().add(new XYChart.Data(t2.getStamp() / 1000.0, t2.getRotation().getAngle()));
    seriesInputData2.getData().add(new XYChart.Data(t3.getStamp() / 1000.0, t3.getRotation().getAngle()));

    XYChart.Series seriesInterpolation2 = new XYChart.Series();
    seriesInterpolation2.setName("Interpolated Lookups");

    TransformStamped response2;

    for (long i = 100; i < 1000; i+=100) {
      response2 = tfb.lookup(i);
      seriesInterpolation2.getData().add(new XYChart.Data(response2.getStamp() / 1000.0, response2.getRotation().getAngle()));
    }

    for (long i = 1100; i < 1500; i+=100) {
      response2 = tfb.lookup(i);
      seriesInterpolation2.getData().add(new XYChart.Data(response2.getStamp() / 1000.0, response2.getRotation().getAngle()));
    }

    XYChart.Series seriesExtrapolation2 = new XYChart.Series();
    seriesExtrapolation2.setName("Extrapolated Lookups");

    for (long i = 1600; i < 2200; i+=100) {
      response2 = tfb.lookup(i);
      seriesExtrapolation2.getData().add(new XYChart.Data(response2.getStamp() / 1000.0, response2.getRotation().getAngle()));
    }

    sc2.getData().addAll(seriesInputData2, seriesInterpolation2, seriesExtrapolation2);

    root.getChildren().addAll(sc, sc2);
    Scene scene  = new Scene(root, 600, 900);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
