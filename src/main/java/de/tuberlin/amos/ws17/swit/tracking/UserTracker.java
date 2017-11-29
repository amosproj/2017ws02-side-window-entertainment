package de.tuberlin.amos.ws17.swit.tracking;

import de.tuberlin.amos.ws17.swit.common.UserExpressions;
import de.tuberlin.amos.ws17.swit.common.UserPosition;

public interface UserTracker {

    boolean getIsUserTracked();

    UserPosition getUserPosition();
    UserExpressions getUserExpressions();

    boolean startTracking();
    boolean stopTracking();


}
