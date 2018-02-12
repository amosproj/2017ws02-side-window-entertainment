package de.tuberlin.amos.ws17.swit.image_analysis;

import com.google.api.services.vision.v1.model.Image;
import javafx.scene.media.Media;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class ImageUtils {

    @Nonnull
    public static Image convertToImage(BufferedImage bufferedImage) throws IOException {
        if (bufferedImage == null) {
            throw new IOException();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        byte[] data = baos.toByteArray();
        return new Image().encodeContent(data);
    }

    @Nullable
    public static BufferedImage getTestImageFile(String imageName) {
        ClassLoader classLoader = ImageUtils.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream("test_images/" + imageName);
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static BufferedImage getRandomTestImage() {
        String[] imageNames = new String[]{"berliner-dom.jpg", "brandenburger-tor.jpg", "brandenburger-tor-2.jpg",
                "fernsehturm.jpg", "fernsehturm-2.jpg", "sieges-saeule.jpg"};
        int rnd = ThreadLocalRandom.current().nextInt(0, imageNames.length);
        ClassLoader classLoader = ImageUtils.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream("test_images/" + imageNames[rnd]);
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Media getTestVideo(String name) {

        try {
            String path = getTestVideoPath(name);
            if (path != null) {
                return new Media(Paths.get(path).toUri().toString());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static String getTestVideoPath(String name) throws URISyntaxException {
        ClassLoader classLoader = ImageUtils.class.getClassLoader();
        URL url = classLoader.getResource(name);
        if (url != null) {
            return new File(url.toURI()).getAbsolutePath();
        }
        return null;
    }

    @Nonnull
    public static BufferedImage cropImage(BufferedImage image, Rectangle rect) {
        try {
            return image.getSubimage(rect.x, rect.y, rect.width, rect.height);
        } catch (RasterFormatException e) {
            System.err.println("Failed to crop image");
            return image;
        }
    }

}
