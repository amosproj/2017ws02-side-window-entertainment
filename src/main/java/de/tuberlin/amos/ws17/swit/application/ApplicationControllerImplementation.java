package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ApplicationControllerImplementation implements ApplicationController {

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private List<PoiViewModel> GpsPois;
    private List<PoiViewModel> LandscapePois;

    private List<PointOfInterest> pointsOfInterest;
    public ObservableList<PoiViewModel> observableList;

    private String title;
    private SimpleStringProperty testSimpleString;

    private SimpleListProperty<PoiViewModel> testSimpleListProperty;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        propertyChangeSupport.firePropertyChange("title", oldTitle, title);
    }

    public ApplicationControllerImplementation() {
        List<PoiViewModel> l = new ArrayList<PoiViewModel>();


        l.add(new PoiViewModel("2"));
        l.add(new PoiViewModel("5"));
        l.add(new PoiViewModel("3"));
        l.add(new PoiViewModel("1"));
        l.add(new PoiViewModel("4"));

        l.add(new PoiViewModel("6"));
        observableList = FXCollections.observableList(l);

        testSimpleString = new SimpleStringProperty("Ist mir ja auch völlig egal");
        setTitle("Ist mir ja völlig egal");

        int min = 10;
        int max = 100;
        setTestSimpleListProperty(new SimpleListProperty<PoiViewModel>());
        for (int i = 0; i < 10; i++) {
            getTestSimpleListProperty().add(new PoiViewModel(Integer.toString(ThreadLocalRandom.current().nextInt(min, max + 1))));
        }
    }

    public void addPOI(int id, String name, BufferedImage image, String information) {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void changeTitle() {
        setTitle("Hello World!");
        setTestSimpleString("Helloa");

        int min = 10;
        int max = 100;
        observableList.add(new PoiViewModel(Integer.toString(ThreadLocalRandom.current().nextInt(min, max + 1))));
        getTestSimpleListProperty().add(new PoiViewModel(Integer.toString(ThreadLocalRandom.current().nextInt(min, max + 1))));
//        PoiViewModel poe = observableList.get(3);
//        observableList.remove(poe);
//        observableList.add(0, poe);
//
//        poe = observableList.get(5);
//        observableList.remove(poe);
//        observableList.add(0, poe);
    }

    public void sortList() {

        getTestSimpleListProperty().sort(new Comparator<PoiViewModel>() {
            @Override
            public int compare(PoiViewModel o1, PoiViewModel o2) {
                return o1.name.compareTo(o2.name) ;
            }
        });

        observableList.sort(new Comparator<PoiViewModel>() {
            @Override
            public int compare(PoiViewModel o1, PoiViewModel o2) {
                return o1.name.compareTo(o2.name) ;
            }
        });
    }

    public SimpleStringProperty getTestSimpleString() {
        return testSimpleString;
    }

    public void setTestSimpleString(String testSimpleString) {
        this.testSimpleString.set(testSimpleString);
    }

    public SimpleListProperty<PoiViewModel> getTestSimpleListProperty() {
        return testSimpleListProperty;
    }

    public void setTestSimpleListProperty(SimpleListProperty<PoiViewModel> testSimpleListProperty) {
        this.testSimpleListProperty = testSimpleListProperty;
    }
}
