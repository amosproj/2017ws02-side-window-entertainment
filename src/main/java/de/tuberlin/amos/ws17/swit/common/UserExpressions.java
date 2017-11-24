package de.tuberlin.amos.ws17.swit.common;

public class UserExpressions {
    private boolean isKiss;
    private boolean isTongueOut;
    private boolean isSmile;
    private boolean isMouthOpen;

    public boolean isKiss() {
        return isKiss;
    }

    public void setKiss(boolean kiss) {
        isKiss = kiss;
    }

    public boolean isTongueOut() {
        return isTongueOut;
    }

    public void setTongueOut(boolean tongueOut) {
        isTongueOut = tongueOut;
    }

    public boolean isSmile() {
        return isSmile;
    }

    public void setSmile(boolean smile) {
        isSmile = smile;
    }

    public boolean isMouthOpen() {
        return isMouthOpen;
    }

    public void setMouthOpen(boolean mouthOpen) {
        isMouthOpen = mouthOpen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserExpressions that = (UserExpressions) o;

        if (isKiss != that.isKiss) return false;
        if (isTongueOut != that.isTongueOut) return false;
        if (isSmile != that.isSmile) return false;
        return isMouthOpen == that.isMouthOpen;
    }

    @Override
    public String toString() {
        return "UserExpressions{" +
            "isKiss=" + isKiss +
            ", isTongueOut=" + isTongueOut +
            ", isSmile=" + isSmile +
            ", isMouthOpen=" + isMouthOpen +
            '}';
    }
}
