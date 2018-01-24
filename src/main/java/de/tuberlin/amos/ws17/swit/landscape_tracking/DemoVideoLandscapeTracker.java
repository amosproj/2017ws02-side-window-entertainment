package de.tuberlin.amos.ws17.swit.landscape_tracking;

import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.media.MediaView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DemoVideoLandscapeTracker implements LandscapeTracker {

    private MediaView mediaView;

    public DemoVideoLandscapeTracker(MediaView mediaView) {
        this.mediaView = mediaView;
    }

    @Override
    public BufferedImage getImage() throws IOException {
        WritableImage wim = mediaView.snapshot(new SnapshotParameters(), null);
        return SwingFXUtils.fromFXImage(wim, null);
    }

    @Override
    public void startModule() throws ModuleNotWorkingException {
        mediaView.setVisible(true);
        mediaView.getMediaPlayer().play();
    }

    @Override
    public boolean stopModule() {
        mediaView.setVisible(false);
        mediaView.getMediaPlayer().stop();
        return true;
    }

    @Override
    public BufferedImage getModuleImage() {
        String path = "";
        try {
            this.getClass().getResource("");
            path = this.getClass().getClassLoader().getResource("module_images/landscape_tracking.jpg").getPath();
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(path);
        }
        return null;
    }

    @Override
    public String getModuleName() {
        return "Landscape Tracker";
    }
}
