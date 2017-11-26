package de.tuberlin.amos.ws17.swit.application;

import java.awt.image.BufferedImage;

public interface ApplicationController {

    /**
     * Sets the view
     * @param view View for the controller
     */
    void setView(ApplicationView view);

    /**
     * Adds a new POI to the list of current POI
     * @param id ID of the POI
     * @param name name of the POI
     * @param image iage of the POI
     * @param information information from the information source about the POI
     */
    void addPOI(int id, String name, BufferedImage image, String information);

    /**
     * Analyze given image and add detected POIs to list
     */
    void analyzeImage();

    /**
     * Capture image from camera
     * @return Image taken from camera
     */
    BufferedImage captureImage();

    /**
     * React when user clicks on POI
     * @param poi clicked POI by user
     */
    void onPoiClicked(PoiViewModel poi);
}
