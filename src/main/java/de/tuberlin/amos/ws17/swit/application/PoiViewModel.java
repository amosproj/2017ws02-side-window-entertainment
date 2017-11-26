package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkResult;

import java.awt.image.BufferedImage;

public class PoiViewModel {

    public String name;
    public BufferedImage image;
    public String informationAbstract;

    public PoiViewModel(String name) {
        this.name = name;
    }

    public PoiViewModel(String name, BufferedImage image, String informationAbstract) {
        this.name = name;
        this.image = image;
        this.informationAbstract = informationAbstract;
    }

    public String toString() {
        return name;
    }

}
