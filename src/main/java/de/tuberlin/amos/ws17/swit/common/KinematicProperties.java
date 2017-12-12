package de.tuberlin.amos.ws17.swit.common;

import org.joda.time.DateTime;

public class KinematicProperties extends GpsPosition{

    private DateTime timeStamp;
    private double course;
    private double velocity;
    private double acceleration;

    public KinematicProperties() {
        this(new DateTime(), 0.0, 0.0, 0.0);
    }

    public KinematicProperties(DateTime timeStamp, double course, double velocity, double acceleration) {
        this.timeStamp = timeStamp;
        this.course = course;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KinematicProperties that = (KinematicProperties) o;

        if (Double.compare(that.getLongitude(), getLongitude()) != 0) return false;
        if (Double.compare(that.getLatitude(), getLatitude()) != 0) return false;
        if (Double.compare(that.course, course) != 0) return false;
        if (Double.compare(that.velocity, velocity) != 0) return false;
        if (Double.compare(that.acceleration, acceleration) != 0) return false;
        return timeStamp != null ? timeStamp.equals(that.timeStamp) : that.timeStamp == null;
    }
}
