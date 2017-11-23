package de.tuberlin.amos.ws17.swit.application;

import java.awt.image.BufferedImage;

public class PoiViewModel {

    public String name;
    public BufferedImage image;
    public String informationAbstract;

    public PoiViewModel(String name) {
        this.name = name;
    }
}
