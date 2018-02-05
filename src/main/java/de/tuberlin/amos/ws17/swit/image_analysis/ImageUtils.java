package de.tuberlin.amos.ws17.swit.image_analysis;

import com.google.api.services.vision.v1.model.Image;
import javafx.scene.media.Media;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class ImageUtils {

    @Nullable
    public static BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

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
    public static BufferedImage convertToBufferedImage(Image image) {
        return createImageFromBytes(image.decodeContent());
    }

    public static void showImage(BufferedImage img, JFrame frame) {
        if (img == null) {
            return;
        }
        ImageIcon icon = new ImageIcon(img);
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth() + 10, img.getHeight() + 20);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
            BufferedImage img = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
//            BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
//            Graphics g = copyOfImage.createGraphics();
//            g.drawImage(img, 0, 0, null);
            return img;
        } catch (RasterFormatException e) {
            return image;
        }
    }

}
