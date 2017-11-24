package de.tuberlin.amos.ws17.swit.common;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class PointOfInterest {
    private String id; //TODO: ist id als String auszudrücken?
    private String name;
    private GpsPosition gpsPosition;
    private BufferedImage image;
    private String informationAbstract;

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

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (gpsPosition != null ? !gpsPosition.equals(that.gpsPosition) : that.gpsPosition != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        return informationAbstract != null ? informationAbstract.equals(that.informationAbstract) : that.informationAbstract == null;
    }
}
