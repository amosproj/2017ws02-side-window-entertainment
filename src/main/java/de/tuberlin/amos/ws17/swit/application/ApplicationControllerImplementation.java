package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class ApplicationControllerImplementation implements ApplicationController {

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private List<PoiViewModel> GpsPois;
    private List<PoiViewModel> LandscapePois;

    private List<PointOfInterest> pointsOfInterest;

    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        propertyChangeSupport.firePropertyChange("title", oldTitle, title);
    }

    public ApplicationControllerImplementation() {
        setTitle("Ist mir ja völlig egal");
    }

    public void addPOI(int id, String name, BufferedImage image, String information) {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void ChangeTitle() {
        setTitle("Hello World!");
    }


}
