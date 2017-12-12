package de.tuberlin.amos.ws17.swit.application.viewmodel;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

public class PoiViewModel {

    private SimpleStringProperty id;
    private SimpleStringProperty name;
    private SimpleObjectProperty<Image> image;
    private SimpleStringProperty informationAbstract;

    public PoiViewModel() {
        this("", "",null, "");
    }

    public PoiViewModel(String id, String name, Image image, String informationAbstract) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.image = new SimpleObjectProperty<Image>(image);
        this.informationAbstract = new SimpleStringProperty(informationAbstract);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Image getImage() {
        return image.get();
    }

    public SimpleObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public String getInformationAbstract() {
        return informationAbstract.get();
    }

    public SimpleStringProperty informationAbstractProperty() {
        return informationAbstract;
    }

    public void setInformationAbstract(String informationAbstract) {
        this.informationAbstract.set(informationAbstract);
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
