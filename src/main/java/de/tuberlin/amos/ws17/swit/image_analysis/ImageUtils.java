package de.tuberlin.amos.ws17.swit.image_analysis;

import com.google.api.services.vision.v1.model.Image;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageUtils {

    public static BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Image convertToImage(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        byte[] data = baos.toByteArray();
        return new Image().encodeContent(data);
    }

    public static BufferedImage convertToBufferedImage(Image image) {
        return createImageFromBytes(image.decodeContent());
    }

    public static void showImage(BufferedImage img, JFrame frame) {
        ImageIcon icon = new ImageIcon(img);
        frame.setLayout(new FlowLayout());
        frame.setSize(img.getWidth(), img.getHeight());
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Nullable
    public static BufferedImage getTestImageFile(String imageName) {
        ClassLoader classLoader = CloudVision.class.getClassLoader();
        URL resourceUrl = classLoader.getResource("test_images/" + imageName);
        if (resourceUrl != null) {
            File file = new File(resourceUrl.getFile());
            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
