package de.tuberlin.amos.ws17.swit.tracking;

import de.tuberlin.amos.ws17.swit.common.UserExpressions;
import de.tuberlin.amos.ws17.swit.common.UserPosition;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Assert;

public class UserTrackerMockTest {

    private static UserTracker userTracker;

    public static void main(String[] args) {

        userTracker = new UserTrackerMock();

        DateTime startDateTime = new DateTime();

        userTracker.startTracking();

        int secondsPassed = 0;

        while (secondsPassed <= 120) {
            System.out.println(secondsPassed);
            //isHardwareAvailable
            Assert.assertEquals(userTracker.isHardwareAvailable(), true);
            //isUserTracked
            if (secondsPassed <= 60) {
                Assert.assertEquals(userTracker.isUserTracked(), true);
            }
            else {
                Assert.assertEquals(userTracker.isUserTracked(), false);
            }
            UserPosition userPosition = userTracker.getUserPosition();
            if (userPosition == null) {
                System.out.println("userPosition=null");
            }
            else {
                System.out.println("userPosition=" + userPosition.toString());
            }

            UserExpressions userExpressions =userTracker.getUserExpressions();
            if (userExpressions == null) {
                System.out.println("userExpressions=null");
            }
            else {
                System.out.println("userExpressions=" + userExpressions.toString());
            }

            //UserPosition
//            UserPosition userPosition = userTracker.getUserPosition();
//            if (!userTracker.isUserTracked()) {
//                Assert.assertEquals(userPosition, null);
//            }
//            else {
//                Assert.assertEquals(userPosition.getHeadCenterPosition().getX(), 0, 0);
//                Assert.assertEquals(userPosition.getHeadCenterPosition().getY(), 0, 0);
//                Assert.assertEquals(userPosition.getHeadCenterPosition().getZ(), 0, 0);
//
//                Assert.assertEquals(userPosition.getLineOfSight().getX(), 0, 0);
//                Assert.assertEquals(userPosition.getLineOfSight().getY(), 0, 0);
//                Assert.assertEquals(userPosition.getLineOfSight().getZ(), 0, 0);
//            }
//
//            //UserExpressions
//            UserExpressions userExpressions =userTracker.getUserExpressions();
//            if (!userTracker.isUserTracked()) {
//                Assert.assertEquals(userExpressions, null);
//            }
//            else {
//                if (secondsPassed >= 10 && secondsPassed < 20) {
//                    Assert.assertEquals(userExpressions.isKiss(), true);
//                    Assert.assertEquals(userExpressions.isTongueOut(), false);
//                    Assert.assertEquals(userExpressions.isSmile(), false);
//                    Assert.assertEquals(userExpressions.isMouthOpen(), false);
//                }
//                else if (secondsPassed >= 20 && secondsPassed < 30) {
//                    Assert.assertEquals(userExpressions.isKiss(), false);
//                    Assert.assertEquals(userExpressions.isTongueOut(), true);
//                    Assert.assertEquals(userExpressions.isSmile(), false);
//                    Assert.assertEquals(userExpressions.isMouthOpen(), false);
//                }
//                else if (secondsPassed >= 30 && secondsPassed < 40) {
//                    Assert.assertEquals(userExpressions.isKiss(), false);
//                    Assert.assertEquals(userExpressions.isTongueOut(), false);
//                    Assert.assertEquals(userExpressions.isSmile(), true);
//                    Assert.assertEquals(userExpressions.isMouthOpen(), false);
//                }
//                else if (secondsPassed >= 40 && secondsPassed < 50) {
//                    Assert.assertEquals(userExpressions.isKiss(), false);
//                    Assert.assertEquals(userExpressions.isTongueOut(), false);
//                    Assert.assertEquals(userExpressions.isSmile(), false);
//                    Assert.assertEquals(userExpressions.isMouthOpen(), true);
//                }
//                else {
//                    Assert.assertEquals(userExpressions.isKiss(), false);
//                    Assert.assertEquals(userExpressions.isTongueOut(), false);
//                    Assert.assertEquals(userExpressions.isSmile(), false);
//                    Assert.assertEquals(userExpressions.isMouthOpen(), false);
//                }
//            }

            secondsPassed = Seconds.secondsBetween(startDateTime, new DateTime()).getSeconds();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        userTracker.stopTracking();
    }
}
