package de.tuberlin.amos.ws17.swit.application.view;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaView;

import java.awt.image.BufferedImage;

public interface ApplicationView {

    void showDebugLog(boolean show);

    void toggleDebugLog();

    MediaView getMediaView();

    void showExpandedPoi(boolean show);

    void toggleLists();
}

