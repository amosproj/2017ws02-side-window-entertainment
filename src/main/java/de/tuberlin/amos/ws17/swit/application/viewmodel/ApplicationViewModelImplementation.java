package de.tuberlin.amos.ws17.swit.application.viewmodel;

import de.tuberlin.amos.ws17.swit.application.view.ApplicationView;
import de.tuberlin.amos.ws17.swit.application.view.ApplicationViewImplementation;
import de.tuberlin.amos.ws17.swit.common.*;
import de.tuberlin.amos.ws17.swit.gps.GpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerFactory;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.ImageUtils;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.information_source.WikiAbstractProvider;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTracker;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerImplementation;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoi;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoiLoader;
import de.tuberlin.amos.ws17.swit.poi.google.GoogleType;
import de.tuberlin.amos.ws17.swit.tracking.JavoNetUserTracker;
import de.tuberlin.amos.ws17.swit.tracking.UserTracker;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationViewModelImplementation implements ApplicationViewModel {

    //Module
    private ApplicationView view;
    private LandmarkDetector cloudVision;
    private LandscapeTracker landscapeTracker;
    private UserTracker userTracker;
    private GpsTracker gpsTracker;
    private DebugLog debugLog = new DebugLog();
    private WikiAbstractProvider abstractProvider;
    //TODO @alle fügt hier die Hauptklassen eures Moduls hinzu, initiiert werden diese aber erst im Konstruktor

    //Threads
    public boolean run;
    public Thread modelviewThread;
    public Thread updateThread;

    //Listen und Binding
    private List<PointOfInterest> pointsOfInterest;
    private PoiViewModel expandedPOI;
    private UserPositionViewModel vmUserPosition;
    private SimpleListProperty<PoiViewModel> propertyPOImaps;
    private SimpleListProperty<PoiViewModel> propertyPOIcamera;
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyCloseButton;
    private SimpleListProperty<String> propertyDebugLog;
    private SimpleListProperty<Image> propertyModuleNotWorkingImageList;
    private SimpleMapProperty<Module, SimpleBooleanProperty> propertyModuleIsWorkingMap;
    private SimpleListProperty<Module> listModuleNotWorking;
    private List<Module> moduleList;

    public Image getPropertyCameraImage() {
        return propertyCameraImage.get();
    }

    public SimpleObjectProperty<Image> propertyCameraImageProperty() {
        return propertyCameraImage;
    }

    private SimpleObjectProperty<Image> propertyCameraImage;
    private BufferedImage cameraImage;


//Konstruktor
    public ApplicationViewModelImplementation(ApplicationView view) {

        propertyCameraImage = new SimpleObjectProperty<Image>();

        //bindDebugLog();

        listModuleNotWorking = new SimpleListProperty<>();
        listModuleNotWorking.set(FXCollections.observableList(new ArrayList<Module>()));

        pointsOfInterest = new ArrayList<PointOfInterest>();
        expandedPOI = new PoiViewModel();
        vmUserPosition = new UserPositionViewModel();

        propertyPOImaps = new SimpleListProperty();
        propertyPOImaps.set(FXCollections.observableList(new ArrayList<PoiViewModel>()));
        propertyPOIcamera = new SimpleListProperty();
        propertyPOIcamera.set(FXCollections.observableList(new ArrayList<PoiViewModel>()));

        propertyModuleNotWorkingImageList = new SimpleListProperty<>();
        propertyModuleNotWorkingImageList.set(FXCollections.observableList(new ArrayList<>()));

        propertyModuleIsWorkingMap = new SimpleMapProperty<>();
        propertyModuleIsWorkingMap.set(FXCollections.observableMap(new HashMap<>()));

        moduleList = new ArrayList<>();

        this.view = view;
        //TODO @alle initiiert hier die Hauptklassen eurer Module

        cloudVision = CloudVision.getInstance();

        //TODO @Vlad Exception handling in deine Klasse (siehe userTracker)
        gpsTracker = GpsTrackerFactory.GetGpsTracker();
        try {
            gpsTracker.startModule();
        }
        catch (ModuleNotWorkingException e){
            e.printStackTrace();
        }

        userTracker = new JavoNetUserTracker();
        userTracker.startTracking();

        landscapeTracker = new LandscapeTrackerImplementation();
        moduleList.add(landscapeTracker);
        abstractProvider = new WikiAbstractProvider();
        moduleList.add(abstractProvider);

        /*
        moduleList.add(cloudVision);
        moduleList.add(userTracker);
        moduleList.add(gpsTracker);
        */

        moduleList.forEach(module -> {
            startModule(module);
        });

        propertyCloseButton = new SimpleObjectProperty<EventHandler<ActionEvent>>();
        propertyCloseButton.set(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DebugLog.log("minimized", this);
                minimizePOI();
            }
        });

        //initTestData();

        run = true;
        modelviewThread = Thread.currentThread();
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int iterations = 10;
                int lastExpression = 0;
                UserExpressions userExpressions;
                while (run) {
                    userExpressions = null;
                    if(userTracker.getIsUserTracked()) {
                        userExpressions = userTracker.getUserExpressions();
                        if(userExpressions.isKiss() /*&& (iterations - lastExpression) >= 10*/) {
                            lastExpression = iterations;
                            BufferedImage image = null;
                            try {
                                image = landscapeTracker.getImage();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (image != null) {
                                cameraImage = image;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        propertyCameraImage.set(SwingFXUtils.toFXImage(cameraImage, null ));
                                    }
                                });
                            }
                            //loadCameraPoi();
                        }
                    }
                    if(iterations % 10 == 0) {
                        //loadCameraPoi();
                        loadMapsPoi();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    iterations++;
                }
            }
        });
        updateThread.start();
    }

    private void startModule(Module module) {
        try {
            module.startModule();
            listModuleNotWorking.remove(module);
        } catch (ModuleNotWorkingException e) {
            listModuleNotWorking.add(module);
        }
    }

    private void addPOIcamera(PointOfInterest poi) {
        PoiViewModel item = convertPOI(poi);
        pointsOfInterest.add(poi);
        propertyPOIcamera.add(item);
    }

    private boolean removePOIcamera(String id) {
        for(PoiViewModel item: propertyPOIcamera) {
            if(item.getId() == id) {
                propertyPOIcamera.remove(item);
                return true;
            }
        }
        return false;
    }

    private void addPOImaps(PointOfInterest poi) {
        PoiViewModel item = convertPOI(poi);
        pointsOfInterest.add(poi);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                propertyPOImaps.add(item);
            }
        });
    }

    private boolean removePOImaps(String id) {
        for(PoiViewModel item: propertyPOImaps) {
            if(item.getId() == id) {
                propertyPOImaps.remove(item);
                return true;
            }
        }
        return false;
    }

    private PoiViewModel convertPOI(PointOfInterest poi) {
        PoiViewModel result = new PoiViewModel();
        result.setId(poi.getId());
        result.setName(poi.getName());
        if(poi.getImage() == null)
            result.setImage(SwingFXUtils.toFXImage(poi.getImage(), null ));
        result.setInformationAbstract(poi.getInformationAbstract());
        return result;
    }

    public boolean expandPOI(String id) {
        for(PoiViewModel item: propertyPOIcamera) {
            if(item.getId() == id) {
                setExpandedPOI(item);
                return true;
            }
        }
        for(PoiViewModel item: propertyPOImaps) {
            if(item.getId() == id) {
                setExpandedPOI(item);
                return true;
            }
        }
        return false;
    }

    public void minimizePOI() {
        expandedPOI.setId("");
        expandedPOI.setName("");
        expandedPOI.setImage(null);
        expandedPOI.setInformationAbstract("");
    }

    private void setExpandedPOI(PoiViewModel item) {
        expandedPOI.setId(item.getId());
        expandedPOI.setName(item.getName());
        expandedPOI.setImage(item.getImage());
        expandedPOI.setInformationAbstract(item.getInformationAbstract());
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
        cameraImage = image;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                propertyCameraImage.set(SwingFXUtils.toFXImage(cameraImage, null ));
            }
        });

        //Analyse Bild
        List<PointOfInterest> pois = cloudVision.identifyPOIs(image);
        if (pois.isEmpty()) {
            return;
        }

        //Abfrage Informationen
        //TODO @JulianS Anfrage an information source mit ermitteltem POI
        for (PointOfInterest poi: pois) {
            poi = abstractProvider.provideAbstract(poi);
        }

        for (PointOfInterest poi: pois) {
            addPOIcamera(poi);
        }
        System.out.println("camera POI Zyklus zu Ende");
    }

    private void loadMapsPoi() {
        KinematicProperties kinematicProperties = new KinematicProperties();

        //double tiergartenLng2=13.33490991;
        //double tiergartenLat2=52.5085468;
        //GpsPosition currentPostion= new GpsPosition(tiergartenLng2, tiergartenLat2);
        //kinematicProperties.setLatitude(tiergartenLat2);
        //kinematicProperties.setLongitude(tiergartenLng2);

        try{
            gpsTracker.fillDumpObject(kinematicProperties);

            DebugLog.log("Latitude: " + kinematicProperties.getLatitude() + " , Longitude: " + kinematicProperties.getLongitude(), this);

            List<PointOfInterest> pois = new ArrayList<PointOfInterest>();
            //Abfrage POIs
            //TODO @Leander Anfrage an das POI Modul, welches eine Liste von POIs in der Nähe zurückgibt
            GooglePoiLoader loader = new GooglePoiLoader(500, 800);
            List<GooglePoi> gPois = loader.loadPlaceForCircleAndType(kinematicProperties,300, GoogleType.food
                    /*,GoogleType.zoo, GoogleType.airport, GoogleType.aquarium, GoogleType.church, GoogleType.city_hall,
                    GoogleType.hospital, GoogleType.library, GoogleType.mosque, GoogleType.museum, GoogleType.park,
                    GoogleType.school, GoogleType.stadium, GoogleType.synagogue, GoogleType.university,
                    GoogleType.point_of_interest, GoogleType.place_of_worship, GoogleType.gas_station, GoogleType.food,
                    GoogleType.restaurant, GoogleType.store*/);
            gPois.addAll(loader.loadPlaceForCircleAndType(kinematicProperties,300, GoogleType.gas_station));

            String names = "";
            if(gPois != null) {
                for (GooglePoi g: gPois) {
                    names += g.getName() + System.lineSeparator();
                    names.length();
                }
            }

            loader.downloadImages(gPois);
            pois = (List) gPois;

            if(pois.size() == 0) {
                return;
            }

            //Abfrage Informationen
            //TODO @JulianS Anfrage an das information source Modul, welches für jeden POI in der Liste die Daten abruft
            for (PointOfInterest poi: pois) {
                poi = abstractProvider.provideAbstract(poi);
            }

            for(PointOfInterest poi: pois) {
                addPOImaps(poi);
            }

        }
        catch (ModuleNotWorkingException e){
            // handle exception
            return;
        }


    }

    private void bindDebugLog() {
        this.propertyDebugLog = new SimpleListProperty<String>();
        this.propertyDebugLog.set(DebugLog.debugLog);

        // systemoutprintline redirect
        System.setOut(new PrintStream(System.out) {

            public void println(String s) {
                propertyDebugLog.add(s);
                super.println(s);
            }

            public void print(String s) {
                propertyDebugLog.add(s);
                super.println(s);
            }
        });
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

    public EventHandler getPropertyCloseButton() {
        return propertyCloseButton.get();
    }

    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyCloseButtonProperty() {
        return propertyCloseButton;
    }

    public void setPropertyCloseButton(EventHandler propertyCloseButton) {
        this.propertyCloseButton.set(propertyCloseButton);
    }

    public ObservableList<String> getPropertyDebugLog() {
        return propertyDebugLog.get();
    }

    public SimpleListProperty<String> propertyDebugLogProperty() {
        return propertyDebugLog;
    }

    public ObservableList<Image> getPropertyModuleNotWorkingImageList() {
        return propertyModuleNotWorkingImageList.get();
    }

    public SimpleListProperty<Image> propertyModuleNotWorkingImageListProperty() {
        return propertyModuleNotWorkingImageList;
    }

    public ObservableList<Module> getListModuleNotWorking() {
        return listModuleNotWorking.get();
    }

    public SimpleListProperty<Module> listModuleNotWorkingProperty() {
        return listModuleNotWorking;
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

//Testdaten
    private void initTestData() {
        List<PoiViewModel> testData = new ArrayList<PoiViewModel>();

        //File domfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/berliner-dom.jpg").getPath());
        Image domimage = new Image("/test_images/berliner-dom.jpg");
        testData.add(new PoiViewModel("5", "Berliner Dom", domimage, "Das ist der Berliner Dom, lalala. Das hier ist ein ganz langer Text um zu testen, " +
                "ob bei einem Label der Text automatisch auf die nächste Zeile springt. Offensichtlich tut er das nur, wenn man eine Variable dafür setzt. "));

        //File torfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/brandenburger-tor.jpg").getPath());
        Image torimg = new Image("/test_images/brandenburger-tor.jpg");
        testData.add(new PoiViewModel("6", "Brandenburger Tor", torimg, "Das Brandenburger Tor. Offensichtlich. " +
                "Wer das nicht kennt muss aber echt unter nem Stein leben. Naja. Infos geb ich dir nicht, solltest du doch alles wissen. Kulturbanause!"));

        //File turmfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/fernsehturm.jpg").getPath());
        Image turmimg = new Image("/test_images/fernsehturm.jpg");
        testData.add(new PoiViewModel("7", "Fernsehturm", turmimg, "Vom Fernsehturm kommt das Fernsehen her. Oder so. " +
                "Heute kommt das Fernsehen aus der Steckdose und stirbt aus. Hah! Video On Demand, hell yeah!"));

        //File siegfile = new File(ApplicationViewImplementation.app.getClass().getResource("/test_images/sieges-saeule.jpg").getPath());
        Image siegimg = new Image("/test_images/sieges-saeule.jpg");
        testData.add(new PoiViewModel("8", "Siegessäule", siegimg, "Die Siegessäule. Da hat wohl jemand was gewonnen und hat direkt mal Geld investiert, " +
                "um es jeden wissen zu lassen. Und jetzt weiß auch du, dass hier irgendwer gewonnen hat. Wahnsinn! " +
                "Noch viel wahnsinniger ist, dass ich mir jetzt einen unglaublich langen Text ausdenken muss um zu sehen, " +
                "ob das Layout der Oberfläche gut funktioniert oder nicht. Dazu möchte ich schauen, ob es eine ScrollBar gibt, " +
                "falls der Text zu lange ist, was ja durchaus vorkommen kann. Vorallem wenn wir das Abstract von Wikipedia anzeigen, " +
                "welches gerne mal sehr lang sein kann. Da muss das natürlich gut funktionieren. Deswegen teste ist das jetzt aus. " +
                "Hoffentlich funktioniert es. Solltest du das hier lesen kann es gut sein, dass es erfolgreich war. "));

        //listPOIcamera = FXCollections.observableList(testData);
        propertyPOIcamera.set(FXCollections.observableList(testData));
    }
}