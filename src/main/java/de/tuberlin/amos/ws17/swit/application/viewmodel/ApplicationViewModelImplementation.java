package de.tuberlin.amos.ws17.swit.application.viewmodel;

import de.tuberlin.amos.ws17.swit.application.PoiCameraThread;
import de.tuberlin.amos.ws17.swit.application.PoiMapsThread;
import de.tuberlin.amos.ws17.swit.application.view.ApplicationView;
import de.tuberlin.amos.ws17.swit.application.view.ApplicationViewImplementation;
import de.tuberlin.amos.ws17.swit.common.KinematicProperties;
import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.common.UserExpressions;
import de.tuberlin.amos.ws17.swit.common.UserPosition;
import de.tuberlin.amos.ws17.swit.gps.GpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerFactory;
import de.tuberlin.amos.ws17.swit.gps.SensorNotFoundException;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTracker;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerImplementation;
import de.tuberlin.amos.ws17.swit.landscape_tracking.WebcamBuilder;
import de.tuberlin.amos.ws17.swit.landscape_tracking.WebcamImplementation;
import de.tuberlin.amos.ws17.swit.tracking.JavoNetUserTracker;
import de.tuberlin.amos.ws17.swit.tracking.UserTracker;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ApplicationViewModelImplementation implements ApplicationViewModel {

    //Module
    public ApplicationView view;
    public LandmarkDetector cloudVision;
    public LandscapeTracker landscapeTracker;
    private WebcamBuilder webcamBuilder;
    private UserTracker userTracker;
    public GpsTracker gpsTracker;
    //TODO @alle fügt hier die Hauptklassen eures Moduls hinzu, initiiert werden diese aber erst im Konstruktor

    //Threads
    public boolean run;
    public Thread modelviewThread;
    public Thread updateThread;

    //Listen und Objekte
    private List<PointOfInterest> pointsOfInterest;
    private PoiViewModel expandedPOI;
    private UserPositionViewModel vmUserPosition;

    //Binding
    private SimpleListProperty<PoiViewModel> propertyPOImaps;
    private SimpleListProperty<PoiViewModel> propertyPOIcamera;

//Konstruktor
    public ApplicationViewModelImplementation(ApplicationView view) {
        this.view = view;
        //TODO @alle initiiert hier die Hauptklassen eurer Module
        WebcamImplementation webcamImplementation = null;
        try {
            webcamImplementation = new WebcamBuilder(10000).setViewSize(new Dimension(640, 480)).build();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        landscapeTracker = new LandscapeTrackerImplementation(webcamImplementation);
        cloudVision = CloudVision.getInstance();
        userTracker = new JavoNetUserTracker();
        userTracker.startTracking();
        //TODO @Vlad Exception handling in deine Klasse (siehe userTracker)
        gpsTracker = GpsTrackerFactory.GetGpsTracker();
        try {
            gpsTracker.start();
        }
        catch (SensorNotFoundException e){
            e.printStackTrace();
        }

        pointsOfInterest = new ArrayList<PointOfInterest>();
        expandedPOI = new PoiViewModel();
        vmUserPosition = new UserPositionViewModel();

        propertyPOImaps = new SimpleListProperty();
        propertyPOIcamera = new SimpleListProperty();

        initTestData();

        run = true;
        modelviewThread = Thread.currentThread();
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int iterations = 0;
                while (run) {
                    trackUser();
                    if(iterations % 10 == 0) {
                        loadCameraPoi();
                        loadMapsPoi();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    iterations++;
                }
            }
        });
    }

    public void addPOI(PointOfInterest poi) {
        //TODO deprecated -> loeschen
    }

    private void addPOIcamera(PoiViewModel poi) {
        //TODO @Magnus work in progress
    }

    private void removePOIcamera(String id) {
        System.out.println(id);
        int index = 0;
        for(PoiViewModel item: propertyPOIcamera) {
            if(item.getId() == id) {
                index = propertyPOIcamera.indexOf(item);
                break;
            }
        }
        propertyPOIcamera.remove(propertyPOIcamera.get(index));
    }

    private void addPOImaps(PoiViewModel poi) {
        //TODO @Magnus work in progress
    }

    private void removePOImaps(String id) {
        System.out.println(id);
        int index = 0;
        for(PoiViewModel item: propertyPOImaps) {
            if(item.getId() == id) {
                index = propertyPOImaps.indexOf(item);
                break;
            }
        }
        propertyPOImaps.remove(propertyPOImaps.get(index));
    }

    private PoiViewModel transormPOI(PointOfInterest poi) {
        PoiViewModel result = new PoiViewModel();
        //TODO @Magnus work in progress
        return result;
    }

    public void expandPOI(String id) {
        //TODO @Magnus umschreiben
        System.out.println(id);
        int index = 0;
        for(PoiViewModel item: propertyPOIcamera) {
            if(item.getId() == id) {
                index = propertyPOIcamera.indexOf(item);
                break;
            }
        }
        setExpandedPOI(propertyPOIcamera.get(index));
    }

    public void minimizePOI() {
        expandedPOI.setId("");
        expandedPOI.setName("");
        expandedPOI.setImage(null);
        expandedPOI.setInformationAbstract("");
        /*expandedPOIname.set("");
        expandedPOIimage.set(null);
        expandedPOIinformationAbstract.set("");*/
    }

    private void setExpandedPOI(PoiViewModel item) {
        expandedPOI.setId(item.getId());
        expandedPOI.setName(item.getName());
        expandedPOI.setImage(item.getImage());
        expandedPOI.setInformationAbstract(item.getInformationAbstract());
        /*expandedPOIname.set(item.getName());
        expandedPOIimage.set(item.getImage());
        expandedPOIinformationAbstract.set(item.getInformationAbstract());*/
    }

    private void loadCameraPoi() {
        BufferedImage image = null;
        //Aufnahme Bild
        try {
            image = landscapeTracker.getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image == null) {
            return;
        }

        PointOfInterest poi = new PointOfInterest();
        //Analyse Bild
        //TODO @Chinh die Funktion analyzeImage soll in deine KLasse, hier soll deine Funktion nur aufgerufen werden
        //PointOfInterest poi = analyzeImage(image);
        if (poi == null) {
            return;
        }

        //Abfrage Informationen
        //TODO @JulianS Anfrage an information source mit ermitteltem POI


        addPOI(poi);
    }

    private void loadMapsPoi() {
        KinematicProperties kinematicProperties = new KinematicProperties();
        //Abfrage GPS Koordinaten
        //TODO @Vlad Anfrage an das GPS Modul stellen, welches die GPS Daten zurückgibt + Ergebnis zurückgeben, anstatt call by reference
        gpsTracker.setDumpObject(kinematicProperties);
        if(kinematicProperties == null) {
            return;
        }

        List<PointOfInterest> pois = new ArrayList<PointOfInterest>();
        //Abfrage POIs
        //TODO @Leander Anfrage an das POI Modul, welches eine Liste von POIs in der Nähe zurückgibt


        if(pois.size() == 0) {
            return;
        }

        //Abfrage Informationen
        //TODO @JulianS Anfrage an das information source Modul, welches für jeden POI in der Liste die Daten abruft


        for(PointOfInterest poi: pois) {
            addPOI(poi);
        }
    }

    private void trackUser() {
        UserPosition userPosition = null;
        UserExpressions userExpressions = null;
        //TODO @Christian User Position vom User Tracking ermittelt
        if(userTracker.getIsUserTracked()) {
            userPosition = userTracker.getUserPosition();
            userExpressions = userTracker.getUserExpressions();
        } else {
            return;
        }

    }

//Getter und Setter
    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public PoiViewModel getExpandedPOI() {
        return expandedPOI;
    }

    public ObservableList<PoiViewModel> getPropertyPOImaps() {
        return propertyPOImaps.get();
    }

    public SimpleListProperty<PoiViewModel> propertyPOImapsProperty() {
        return propertyPOImaps;
    }

    public void setPropertyPOImaps(ObservableList<PoiViewModel> propertyPOImaps) {
        this.propertyPOImaps.set(propertyPOImaps);
    }

    public ObservableList<PoiViewModel> getPropertyPOIcamera() {
        return propertyPOIcamera.get();
    }

    public SimpleListProperty<PoiViewModel> propertyPOIcameraProperty() {
        return propertyPOIcamera;
    }

    public void setPropertyPOIcamera(ObservableList<PoiViewModel> propertyPOIcamera) {
        this.propertyPOIcamera.set(propertyPOIcamera);
    }

//Testdaten
    private void initTestData() {
        List<PoiViewModel> testData = new ArrayList<PoiViewModel>();

        File domfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/berliner-dom.jpg").getPath());
        Image domimage = new Image(domfile.toURI().toString());
        testData.add(new PoiViewModel("5", "Berliner Dom", domimage, "Das ist der Berliner Dom, lalala. Das hier ist ein ganz langer Text um zu testen, " +
                "ob bei einem Label der Text automatisch auf die nächste Zeile springt. Offensichtlich tut er das nur, wenn man eine Variable dafür setzt. "));

        File torfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/brandenburger-tor.jpg").getPath());
        Image torimg = new Image(torfile.toURI().toString());
        testData.add(new PoiViewModel("6", "Brandenburger Tor", torimg, "Das Brandenburger Tor. Offensichtlich. " +
                "Wer das nicht kennt muss aber echt unter nem Stein leben. Naja. Infos geb ich dir nicht, solltest du doch alles wissen. Kulturbanause!"));

        File turmfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/fernsehturm.jpg").getPath());
        Image turmimg = new Image(turmfile.toURI().toString());
        testData.add(new PoiViewModel("7", "Fernsehturm", turmimg, "Vom Fernsehturm kommt das Fernsehen her. Oder so. " +
                "Heute kommt das Fernsehen aus der Steckdose und stirbt aus. Hah! Video On Demand, hell yeah!"));

        File siegfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/sieges-saeule.jpg").getPath());
        Image siegimg = new Image(siegfile.toURI().toString());
        testData.add(new PoiViewModel("8", "Siegessäule", siegimg, "Die Siegessäule. Da hat wohl jemand was gewonnen und hat direkt mal Geld investiert, " +
                "um es jeden wissen zu lassen. Und jetzt weiß auch du, dass hier irgendwer gewonnen hat. Wahnsinn!"));

        //listPOIcamera = FXCollections.observableList(testData);
        propertyPOIcamera.set(FXCollections.observableList(testData));
    }

    /*@Override
    public void analyzeImage() {
        BufferedImage image = captureImage();
        new Thread(() -> {
            try {
                List<LandmarkResult> landmarks = cloudVision.identifyLandmarks(image, 5);
                // update UI on FX thread
                Platform.runLater(() -> {
                    for (LandmarkResult l : landmarks) {
                        PoiViewModel poi = new PoiViewModel(l.getId(), l.getName(), l.getCroppedImage(), "");
                        poi.setId(l.getId());
                        testSimpleListProperty.add(0, poi);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public BufferedImage captureImage() {
        BufferedImage testImage = getRandomTestImage();
        return testImage;
    }

    @Override
    public void onPoiClicked(PoiViewModel poi) {
        if (!StringUtils.isEmpty(poi.informationAbstract)) {
            view.showPoiInfo(poi);
        } else {
            new Thread(() -> {
                InformationProvider kgs = KnowledgeGraphSearch.getInstance();
                String info = poi.id != null ? kgs.getInfoById(poi.id) : kgs.getInfoByName(poi.name);
                Platform.runLater(() -> {
                    poi.informationAbstract = info;
                    view.showPoiInfo(poi);
                });
            }).start();
        }
    }*/
}