package de.tuberlin.amos.ws17.swit.tracking;

import de.tuberlin.amos.ws17.swit.common.DebugLog;
import de.tuberlin.amos.ws17.swit.common.UserExpressions;
import de.tuberlin.amos.ws17.swit.common.UserPosition;
import de.tuberlin.amos.ws17.swit.common.Vector3D;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Seconds;

public class UserTrackerMock implements UserTracker, Runnable {

    private boolean isTracking = false;
    private DateTime simulatedStartDateTime = null;
    private DateTime simulatedDateTime = null;
    private DateTime startDateTime = null;

    @Override
    public boolean isHardwareAvailable() {
        return true;
    }

    @Override
    public boolean isUserTracked() {
        if (!isTracking)
            return false;

        if (simulatedDateTime.minuteOfHour().get() % 2 == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public UserPosition getUserPosition() {
        if (!isUserTracked()) {
            return null;
        }
        //TODO: da sollte noch mehr zurückgegeben aber aktuell noch nicht wichtig
        return new UserPosition(new Vector3D(0,0,0), new Vector3D(0,0,0));
    }

    @Override
    public UserExpressions getUserExpressions() {
        if (!isUserTracked()) {
            return null;
        }

        if (simulatedDateTime.secondOfMinute().get() >= 5 && simulatedDateTime.secondOfMinute().get() < 10) {
            return new UserExpressions(true, false, false, false);
        }
        else if (simulatedDateTime.secondOfMinute().get() >= 10 && simulatedDateTime.secondOfMinute().get() < 15) {
            return new UserExpressions(false, true, false, false);
        }
        else if (simulatedDateTime.secondOfMinute().get() >= 15 && simulatedDateTime.secondOfMinute().get() < 20) {
            return new UserExpressions(false, false, true, false);
        }
        else if (simulatedDateTime.secondOfMinute().get() >= 25 && simulatedDateTime.secondOfMinute().get() < 30) {
            return new UserExpressions(false, false, false, true);
        }

        return new UserExpressions(false, false, false, false);
    }

    @Override
    public boolean startTracking() {
        simulatedStartDateTime = new DateTime(2017,12,13,12,0,0);
        simulatedDateTime = new DateTime(2017,12,13,12,0,0);
        isTracking = true;

        Thread threadLoop = new Thread(this);
        threadLoop.start();
        DebugLog.log("Tracking Thread started");
        return true;
    }

    @Override
    public boolean stopTracking() {
        isTracking = false;
        DebugLog.log("Tracking Thread stopped");
        return true;
    }

    private void refreshSimulatedDateTime() {
        Duration durationSinceStartDateTime = Seconds.secondsBetween(startDateTime, new DateTime()).toStandardDuration();
        simulatedDateTime = simulatedStartDateTime.plus(durationSinceStartDateTime);
    }

    @Override
    public void run() {
        startDateTime = new DateTime();
        while (isTracking) {
            refreshSimulatedDateTime();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
