package de.tuberlin.amos.ws17.swit.application;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.awt.image.BufferedImage;

public interface ApplicationView {

    /*
     * Adds the specified POI to the view of camera POI at the top.
     * @param id ID of the POI that is to be displayed
     * @param name the name of the POI
     * @param image the image of the POI
     * @param information information from the information source
     */
    void displayCameraPOI(int id, String name, Image image, String information);

    /*
     * Adds the specified POI to the view of POI from maps at the bottom
     * @param id ID of the POI
     * @param name name of the POI
     * @param information information from the information source
     */
    void displayMapsPOI(int id, String name, String information);

    /*
     * Removes the specified POI from the list (either top or bottom)
     * œparam id ID of the POI
     */
    void removePOI(int id);

    /*
     * Displays the POI in the middle of the screen with more information
     * @param id ID of the POI
     */
    void expandPOI(int id);

    /*
     * Closes the currently expanded POI
     * @return returns true if the POI was closed and false if no POI was expanded
     */
    boolean minimizePOI();
}
