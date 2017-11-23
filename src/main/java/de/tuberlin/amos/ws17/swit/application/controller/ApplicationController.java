package de.tuberlin.amos.ws17.swit.application.controller;

import java.awt.image.BufferedImage;

public interface ApplicationController {

    /*
     * Adds a new POI to the list of current POI
     * @param id ID of the POI
     * @param name name of the POI
     * @param image iage of the POI
     * @param information information from the information source about the POI
     */
    void addPOI(int id, String name, BufferedImage image, String information);


}
