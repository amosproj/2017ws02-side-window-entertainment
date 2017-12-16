package de.tuberlin.amos.ws17.swit.application.viewmodel;

import javafx.scene.image.Image;

public enum ExpressionType {

    KISS("Kuss", "/expression_images/expression_kiss.png"),
    SMILE("Lachen", "/expression_images/expression_smile.png"),
    TONGUEOUT("Zunge draußen", "/expression_images/expression_tongueout.png"),
    MOUTHOPEN("Mund offen", "/expression_images/expression_mouthopen.png");

    private final String name;
    private final Image image;

    ExpressionType(String name, String imagePath) {
        this.name = name;
        image = new Image(imagePath);
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
