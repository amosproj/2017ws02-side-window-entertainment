package de.tuberlin.amos.ws17.swit.tracking;

public class UserExpressions {
    public boolean isTracked;

    public boolean isKiss;
    public boolean isTongueOut;
    public boolean isSmile;
    public boolean isMouthOpen;

    public String toString()
    {
        String result = "[UserExpressions]" + System.lineSeparator();
        result += "isTracked: " + Boolean.toString(isTracked) + System.lineSeparator();;

        if (isTracked)
        {
            result += "isKiss: " + Boolean.toString(isKiss) + System.lineSeparator();;
            result += "isTongueOut: " + Boolean.toString(isTongueOut) + System.lineSeparator();;
            result += "isSmile: " + Boolean.toString(isSmile) + System.lineSeparator();;
            result += "isMouthOpen: " + Boolean.toString(isMouthOpen) + System.lineSeparator();;
        }

        return result;
    }
}
