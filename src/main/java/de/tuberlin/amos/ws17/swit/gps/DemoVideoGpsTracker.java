package de.tuberlin.amos.ws17.swit.gps;

import de.tuberlin.amos.ws17.swit.common.GpsPosition;
import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Stack;

public class DemoVideoGpsTracker implements GpsTracker {

    private Stack<KinematicProperties>      gpsStack = new Stack<>();
    private LinkedList<KinematicProperties> history  = new LinkedList<>();
    private de.tuberlin.amos.ws17.swit.common.GpsPosition currentPosition;

    public DemoVideoGpsTracker() throws IOException {
        // read json
        fillStack();
    }

    private void fillStack() throws IOException {
        InputStream is = DemoVideoGpsTracker.class.getClassLoader().getResourceAsStream("demo_video_gps.json");
        String jsonTxt = IOUtils.toString(is);

        JSONObject jsnobject = new JSONObject(jsonTxt);

        JSONArray jsonArray = jsnobject.getJSONArray("locations");
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            JSONObject locationObject = jsonArray.getJSONObject(i);
            double lat = locationObject.getDouble("lat");
            double lng = locationObject.getDouble("lng");
            KinematicProperties kinematicProperties = new KinematicProperties();
            kinematicProperties.setLatitude(lat);
            kinematicProperties.setLongitude(lng);
            gpsStack.push(kinematicProperties);
        }
    }

    @Override
    public GpsPosition getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public KinematicProperties fillDumpObject(KinematicProperties kinProps) {
        if (gpsStack.isEmpty()) {
            try {
                fillStack();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        kinProps = gpsStack.pop();
        history.add(kinProps);
        currentPosition = new GpsPosition(kinProps.getLongitude(), kinProps.getLatitude());
        return kinProps;
    }

    @Override
    public LinkedList<KinematicProperties> getGpsTrack(int count) {
        return history;
    }

    @Override
    public double getDistanceTravelled() {
        return 0;
    }

    @Override
    public long getTimePassed() {
        return 0;
    }

    @Override
    public void startModule() {

    }

    @Override
    public boolean stopModule() {
        return false;
    }

    @Override
    public BufferedImage getModuleImage() {
        return null;
    }

    @Override
    public String getModuleName() {
        return "GPS Tracker";
    }
}
