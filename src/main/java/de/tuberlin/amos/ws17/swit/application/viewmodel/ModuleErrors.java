package de.tuberlin.amos.ws17.swit.application.viewmodel;

import javafx.scene.image.Image;

public enum ModuleErrors {
    NOGPSHARDWARE("GPS Sensor", "/error_images/error_nogpshardware.png"),
    NOCAMERA("Außenkamera", "/error_images/error_nocamera.png"),
    NOUSERCAMERA("Innenkamera", "/error_images/error_nousercamera.png"),
    NOINTERNET("Internetverbindung", "/error_images/error_nointernet.jpg");

    private final String name;
    private final Image image;

    ModuleErrors(String name, String imagePath) {
        this.name = name;
        image = new Image(imagePath);
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
