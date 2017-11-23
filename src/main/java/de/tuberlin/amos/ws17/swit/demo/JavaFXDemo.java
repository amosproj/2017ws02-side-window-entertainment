package de.tuberlin.amos.ws17.swit.demo;

import de.tuberlin.amos.ws17.swit.application.ApplicationViewImplementation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class JavaFXDemo extends Application {

    public static void main(String[] args) {
        Thread thread = new Thread() {
            public void run() {
                Application.launch(ApplicationViewImplementation.class, args);
            }
        };
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                File domfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/berliner-dom.jpg").getPath());
                Image domimage = new Image(domfile.toURI().toString());
                ApplicationViewImplementation.app.displayCameraPOI(5, "Berliner Dom", domimage, "Das ist der Berliner Dom, lalala. Das hier ist ein ganz langer Text um zu testen, " +
                        "ob bei einem Label der Text automatisch auf die nächste Zeile springt. Offensichtlich tut er das nur, wenn man eine Variable dafür setzt. ");

                File torfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/brandenburger-tor.jpg").getPath());
                Image torimg = new Image(torfile.toURI().toString());
                ApplicationViewImplementation.app.displayCameraPOI(6, "Brandenburger Tor", torimg, "Das Brandenburger Tor. Offensichtlich. " +
                        "Wer das nicht kennt muss aber echt unter nem Stein leben. Naja. Infos geb ich dir nicht, solltest du doch alles wissen. Kulturbanause!");

                File turmfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/fernsehturm.jpg").getPath());
                Image turmimg = new Image(turmfile.toURI().toString());
                ApplicationViewImplementation.app.displayCameraPOI(7, "Fernsehturm", turmimg, "Vom Fernsehturm kommt das Fernsehen her. Oder so. " +
                        "Heute kommt das Fernsehen aus der Steckdose und stirbt aus. Hah! Video On Demand, hell yeah!");

                File siegfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/sieges-saeule.jpg").getPath());
                Image siegimg = new Image(siegfile.toURI().toString());
                ApplicationViewImplementation.app.displayCameraPOI(8, "Siegessäule", siegimg, "Die Siegessäule. Da hat wohl jemand was gewonnen und hat direkt mal Geld investiert, " +
                        "um es jeden wissen zu lassen. Und jetzt weiß auch du, dass hier irgendwer gewonnen hat. Wahnsinn!");
            }
        });

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
