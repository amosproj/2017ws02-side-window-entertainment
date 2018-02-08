package de.tuberlin.amos.ws17.swit.common;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {

    public static void slideUp(Node node, double distance, double duration, boolean visibleAfter) {
        node.setVisible(true);
        TranslateTransition transition = new TranslateTransition(Duration.millis(duration), node);
        transition.setToY(node.getTranslateY() - distance);
        transition.setCycleCount(1);
        transition.setOnFinished(event -> node.setVisible(visibleAfter));
        transition.play();
    }

    public static void slideDown(Node node, double distance, double duration, boolean visibleAfter) {
        slideUp(node, -distance, duration, visibleAfter);
    }

    public static void scaleUp(Node node, double fromX, double toX, double fromY, double toY, double duration, boolean visibleAfter) {
        node.setVisible(true);
        ScaleTransition st = new ScaleTransition(Duration.millis(duration), node);
        st.setFromX(fromX);
        st.setToX(toX);
        st.setFromY(fromY);
        st.setToY(toY);
        st.setOnFinished(event -> node.setVisible(visibleAfter));
        st.play();
    }

    public static void scaleDown(Node node, double toX, double toY, double duration, boolean visibleAfter) {
        scaleUp(node, 1, toX, 1, toY, duration, visibleAfter);
    }

    public static void fadeIn(Node node, double duration) {
        node.setVisible(true);
        fade(node, 0f, 1f, duration, true);
    }

    public static void fadeOut(Node node, double duration) {
        fade(node, 1f, 0f, duration, false);
    }

    private static void fade(Node node, double fromValue, double toValue, double duration, boolean visibleAfter) {
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);
        ft.setOnFinished(event -> node.setVisible(visibleAfter));
        ft.play();
    }
}
