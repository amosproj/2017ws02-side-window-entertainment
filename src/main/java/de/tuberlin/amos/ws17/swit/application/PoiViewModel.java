package de.tuberlin.amos.ws17.swit.application;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class PoiViewModel {

    private String id;
    private String name;
    private Image image;
    private String informationAbstract;

    public PoiViewModel() {
        id = "";
        name = "";
        informationAbstract = "";
    }

    public PoiViewModel(String name) {
        this.name = name;
    }

    public PoiViewModel(String id, String name, Image image, String informationAbstract) {
        this.id = id;
        this.name = name;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
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

        PoiViewModel that = (PoiViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        return informationAbstract != null ? informationAbstract.equals(that.informationAbstract) : that.informationAbstract == null;
    }
}
