package de.tuberlin.amos.ws17.swit.application;

import de.tuberlin.amos.ws17.swit.common.PointOfInterest;
import de.tuberlin.amos.ws17.swit.gps.GpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerFactory;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerImplementation;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkResult;
import de.tuberlin.amos.ws17.swit.information_source.InformationProvider;
import de.tuberlin.amos.ws17.swit.information_source.KnowledgeGraphSearch;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils.getRandomTestImage;

public class ApplicationControllerImplementation implements ApplicationController {

    //Module
    public ApplicationView view;
    public LandmarkDetector cloudVision;
    //TODO @alle fügt hier die Hauptklassen eures Moduls hinzu, initiiert werden diese aber erst im Konstruktor

    public GpsTracker gpsTracker;

    //Threads
    public Object lock;
    public boolean run;
    public PoiMapsThread mapsThread;
    public PoiCameraThread cameraThread;
    public Thread controllerThread;

    //Listen und Objekte
    private List<PointOfInterest> pointsOfInterest;
    private PoiViewModel expandedPOI;

    //Binding
    private SimpleStringProperty expandedPOIname;
    private SimpleObjectProperty<Image> expandedPOIimage;
    private SimpleStringProperty expandedPOIinformationAbstract;
    private SimpleListProperty<PoiViewModel> propertyPOImaps;
    private SimpleListProperty<PoiViewModel> propertyPOIcamera;

//Konstruktor
    public ApplicationControllerImplementation(ApplicationView view) {
        this.view = view;
        //cloudVision = CloudVision.getInstance();
        //TODO @alle initiiert hier die Hauptklassen eurer Module
        gpsTracker = GpsTrackerFactory.GetGpsTracker();
        gpsTracker.start();

        pointsOfInterest = new ArrayList<PointOfInterest>();
        expandedPOI = new PoiViewModel();

        expandedPOIname = new SimpleStringProperty();
        expandedPOIimage = new SimpleObjectProperty();
        expandedPOIinformationAbstract = new SimpleStringProperty();
        propertyPOImaps = new SimpleListProperty();
        propertyPOIcamera = new SimpleListProperty();

        initTestData();

        lock = new Object();
        run = true;
        mapsThread = new PoiMapsThread(this);
        cameraThread = new PoiCameraThread(this);
        controllerThread = Thread.currentThread();
        //TODO @Magnus Threadsteuerung
    }

    public void addPOI(PointOfInterest poi) {
        //TODO @Magnus work in progress
    }

    public void removePOIcamera(String id) {
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

    public void addPOIcamera(PoiViewModel poi) {
        //TODO @Magnus work in progress
    }

    public void removePOImaps(String id) {
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

    public void expandPOI(String id) {
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
        expandedPOI = null;
        expandedPOIname.set("");
        expandedPOIimage.set(null);
        expandedPOIinformationAbstract.set("");
    }

    private void setExpandedPOI(PoiViewModel item) {
        expandedPOI = item;
        expandedPOIname.set(item.getName());
        expandedPOIimage.set(item.getImage());
        expandedPOIinformationAbstract.set(item.getInformationAbstract());
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

    public String getExpandedPOIname() {
        return expandedPOIname.get();
    }

    public SimpleStringProperty expandedPOInameProperty() {
        return expandedPOIname;
    }

    public void setExpandedPOIname(String expandedPOIname) {
        this.expandedPOIname.set(expandedPOIname);
    }

    public Image getExpandedPOIimage() {
        return expandedPOIimage.get();
    }

    public SimpleObjectProperty<Image> expandedPOIimageProperty() {
        return expandedPOIimage;
    }

    public void setExpandedPOIimage(Image expandedPOIimage) {
        this.expandedPOIimage.set(expandedPOIimage);
    }

    public String getExpandedPOIinformationAbstract() {
        return expandedPOIinformationAbstract.get();
    }

    public SimpleStringProperty expandedPOIinformationAbstractProperty() {
        return expandedPOIinformationAbstract;
    }

    public void setExpandedPOIinformationAbstract(String expandedPOIinformationAbstract) {
        this.expandedPOIinformationAbstract.set(expandedPOIinformationAbstract);
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