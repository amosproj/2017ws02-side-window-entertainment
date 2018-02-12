package de.tuberlin.amos.ws17.swit.application.view;

import org.pdfsam.ui.RingProgressIndicator;

public class RingProgressView extends RingProgressIndicator {

    public RingProgressView() {
        this.getStylesheets().clear();
        this.getStylesheets().add("/stylesheets/ringprogress.css");
        this.getStylesheets().add("/stylesheets/circleprogress.css");
    }
}
