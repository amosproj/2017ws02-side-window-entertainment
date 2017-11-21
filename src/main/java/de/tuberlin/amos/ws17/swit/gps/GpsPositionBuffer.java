package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.application.TransformStamped;
import de.tuberlin.amos.ws17.swit.application.TransformationBuffer;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class GpsPositionBuffer {
  public GpsPositionBuffer() {
    // keep GPS positions in cache for 30 seconds
    buffer = new TransformationBuffer(30);
  }

  public void insertNewGpsFix(GpsPosition fix) {
    // convert geographic coordinate to UTM referenced position
    LatLng latLng = new LatLng(fix.getLatitude(), fix.getLongitude());
    UTMRef utmCoord = latLng.toUTMRef();
    TransformStamped pos = new TransformStamped(fix.getTimeStamp(), Rotation.IDENTITY, new Vector3D(utmCoord.getEasting(), utmCoord.getNorthing(), 0.0));

    // insert into buffer
    buffer.insert(pos);
  }

  public TransformStamped lookupUtmVehiclePosition(long stamp) {
    return buffer.lookup(stamp);
  }

  private TransformationBuffer buffer;
}
