package de.tuberlin.amos.ws17.swit.application;

import java.awt.image.BufferedImage;

public class PointOfInterest {

    private int id;
    private String name;
    private BufferedImage image;
    private String informationAbstract;

    public int getId() {
        return id;
    }

    public void SetId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        return informationAbstract != null ? informationAbstract.equals(that.informationAbstract) : that.informationAbstract == null;
    }
}
