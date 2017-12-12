package de.tuberlin.amos.ws17.swit.common;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class PointOfInterest {

    private String id;
    private String name;
    private GpsPosition gpsPosition;
    private BufferedImage image;
    private String informationAbstract;

    public PointOfInterest() {
        this("", "", new GpsPosition(), null, "");
    }

    public PointOfInterest(String id, String name, GpsPosition gpsPosition) {
        this(id, name, gpsPosition, null, "");
    }

    public PointOfInterest(String id, String name, GpsPosition gpsPosition, BufferedImage image, String informationAbstract) {
        this.id = id;
        this.name = name;
        this.gpsPosition = gpsPosition;
        this.image = image;
        this.informationAbstract = informationAbstract;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GpsPosition getGpsPosition() {
        return gpsPosition;
    }

    public void setGpsPosition(GpsPosition gpsPosition) {
        this.gpsPosition = gpsPosition;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getInformationAbstract() {
        return informationAbstract;
    }

    public void setInformationAbstract(String informationAbstract) {
        this.informationAbstract = informationAbstract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointOfInterest that = (PointOfInterest) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gpsPosition=" + gpsPosition +
                ", image=" + image +
                ", informationAbstract='" + informationAbstract + '\'' +
                '}';
    }
}
