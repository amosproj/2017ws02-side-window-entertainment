package de.tuberlin.amos.ws17.swit.application.view;

import javafx.scene.control.Skin;
import org.pdfsam.ui.RingProgressIndicator;

public class RingProgressView extends RingProgressIndicator {

    public RingProgressView() {
        this.getStylesheets().clear();
        this.getStylesheets().add("/stylesheets/ringprogress.css");
        this.getStylesheets().add("/stylesheets/circleprogress.css");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NoLabelRingProgressViewSkin(this);
    }
}
