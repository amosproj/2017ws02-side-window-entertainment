package de.tuberlin.amos.ws17.swit.application.viewmodel;

import de.tuberlin.amos.ws17.swit.application.view.ApplicationView;
import de.tuberlin.amos.ws17.swit.common.*;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.gps.GpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerImplementation;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerMock;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.information_source.InformationProvider;
import de.tuberlin.amos.ws17.swit.information_source.KnowledgeGraphSearch;
import de.tuberlin.amos.ws17.swit.information_source.WikiAbstractProvider;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTracker;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerImplementation;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerMock;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoi;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoiService;
import de.tuberlin.amos.ws17.swit.tracking.JavoNetUserTracker;
import de.tuberlin.amos.ws17.swit.tracking.UserTracker;
import de.tuberlin.amos.ws17.swit.tracking.UserTrackerMock;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.layout.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class ApplicationViewModelImplementation implements ApplicationViewModel {

    //Module
    private ApplicationView view;
    private LandmarkDetector cloudVision;
    private LandscapeTracker landscapeTracker;
    private UserTracker userTracker;
    private GpsTracker gpsTracker;
    private DebugLog debugLog = new DebugLog();
    private WikiAbstractProvider abstractProvider;
    private GooglePoiService googlePoiService;
    private InformationProvider knowledgeGraphSearch;

    //Threads
    public boolean run;
    private Thread modelviewThread;
    private Thread updateThread;
    private Thread cameraThread;
    private Thread mapsThread;

    //Listen und Binding
    private List<PointOfInterest> pointsOfInterest;
    private PoiViewModel expandedPOI;
    private UserPositionViewModel vmUserPosition;
    private SimpleListProperty<PoiViewModel> propertyPOImaps;
    private SimpleListProperty<PoiViewModel> propertyPOIcamera;
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyCloseButton;
    private SimpleListProperty<String> propertyDebugLog;
    private SimpleListProperty<ModuleStatusViewModel> listModuleStatus;
    private SimpleListProperty<UserExpressionViewModel> listExpressionStatus;
    private List<Module> moduleList;

    public Image getPropertyCameraImage() {
        return propertyCameraImage.get();
    }

    public SimpleObjectProperty<Image> propertyCameraImageProperty() {
        return propertyCameraImage;
    }

    private SimpleObjectProperty<Image> propertyCameraImage;
    public Image cameraImage;
    public Property<Background> backgroundProperty;
    public BackgroundImage backgroundImage;
    public Background background;

    private Properties properties;

//Konstruktor
    public ApplicationViewModelImplementation(ApplicationView view) {
        initProperties();
        initObjects(view);
        initModules();

        run = true;
        modelviewThread = Thread.currentThread();
        initUpdateThread();
        initMapsThread();
        initCameraThread();

        backgroundProperty = new SimpleObjectProperty<>();

        //initTestData();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraImage = SwingFXUtils.toFXImage(landscapeTracker.getImage(), null );
                    backgroundImage = new BackgroundImage(cameraImage,
                            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT);
                    background = new Background(backgroundImage);
                    backgroundProperty.setValue(background);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        updateThread.start();
    }

    private void initProperties() {
        properties = new Properties();
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream("hardware.properties");
            properties.load(input);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initObjects(ApplicationView view) {
        this.view = view;

        propertyCameraImage = new SimpleObjectProperty<Image>();

        //bindDebugLog();

        listModuleStatus = new SimpleListProperty<>();
        listModuleStatus.set(FXCollections.observableList(new ArrayList<ModuleStatusViewModel>()));

        listExpressionStatus = new SimpleListProperty<>();
        listExpressionStatus.set(FXCollections.observableList(new ArrayList<UserExpressionViewModel>()));

        pointsOfInterest = new ArrayList<PointOfInterest>();
        expandedPOI = new PoiViewModel();
        vmUserPosition = new UserPositionViewModel();

        propertyPOImaps = new SimpleListProperty();
        propertyPOImaps.set(FXCollections.observableList(new ArrayList<PoiViewModel>()));
        propertyPOIcamera = new SimpleListProperty();
        propertyPOIcamera.set(FXCollections.observableList(new ArrayList<PoiViewModel>()));

        moduleList = new ArrayList<>();

        propertyCloseButton = new SimpleObjectProperty<EventHandler<ActionEvent>>();
        propertyCloseButton.set(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DebugLog.log("minimized", this);
                minimizePOI();
            }
        });
    }

    private void initModules() {
        //GPS
        if(properties.get("gpsmodule").equals("1")) {
            try {
                System.out.println("loading GpsTracker...");
                gpsTracker = new GpsTrackerImplementation();
                moduleList.add(gpsTracker);
                gpsTracker.startModule();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
            }
            catch (ModuleNotWorkingException e){
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            }
        } else if(properties.get("gpsmodule").equals("0")) {
            try {
                System.out.println("loading GpsTrackerMock...");
                gpsTracker = new GpsTrackerMock();
                moduleList.add(gpsTracker);
                gpsTracker.startModule();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
            } catch (ModuleNotWorkingException e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            }
        } else {
            System.out.println("failed to load GpsTracker");
        }

        //User Tracking
        if(properties.getProperty("usercamera").equals("1")) {
            try {
                System.out.println("loading UserTracker...");
                userTracker = new JavoNetUserTracker();
                userTracker.startTracking();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, true);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, false);
            }
        } else if(properties.getProperty("usercamera").equals("0")) {
            try {
                System.out.println("loading UserTrackerMock...");
                userTracker = new UserTrackerMock();
                userTracker.startTracking();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, true);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, false);
            }
        } else {
            System.out.println("failed to load UserTracker");
        }

        //Landscape Tracking
        if(properties.getProperty("camera").equals("1")) {
            try {
                System.out.println("loading LandscapeTracker...");
                landscapeTracker = new LandscapeTrackerImplementation();
                moduleList.add(landscapeTracker);
                landscapeTracker.startModule();
                setModuleStatus(ModuleErrors.NOCAMERA, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            }
        } else if(properties.getProperty("camera").equals("0")) {
            try {
                System.out.println("loading LandscapeTrackerMock...");
                landscapeTracker = new LandscapeTrackerMock();
                moduleList.add(landscapeTracker);
                landscapeTracker.startModule();
                setModuleStatus(ModuleErrors.NOCAMERA, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            }
        } else {
            System.out.println("failed to load LandscapeTracker");
        }

        //CloudVision
        if(properties.getProperty("image_analysis").equals("1")) {
            try {
                System.out.println("loading CloudVision...");
                cloudVision = CloudVision.getInstance();
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            }
        } else if(properties.getProperty("image_analysis").equals("0")) {
            setModuleStatus(ModuleErrors.NOINTERNET, false);
        } else {
            System.out.println("failed to load CloudVision");
        }

        //Information Source
        if(properties.getProperty("information_source").equals("1")) {
            try {
                System.out.println("loading AbstractProvider...");
                abstractProvider = new WikiAbstractProvider();
                moduleList.add(abstractProvider);
                abstractProvider.startModule();
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            }
        } else if(properties.getProperty("information_source").equals("0")) {
            setModuleStatus(ModuleErrors.NOINTERNET, false);
        } else {
            System.out.println("failed to load AbstractProvider");
        }

        //Google KnowledgeGraphSearch
        if(properties.getProperty("information_source").equals("1")) {
            try {
                System.out.println("loading KnowledgeGraphSearch...");
                knowledgeGraphSearch = KnowledgeGraphSearch.getInstance();
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            } catch(Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            }
        } else if(properties.getProperty("information_source").equals("0")) {
            setModuleStatus(ModuleErrors.NOINTERNET, false);
        } else {
            System.out.println("failed to load KnowledgeGraphSearch");
        }

        //Google POI loader
        if(properties.getProperty("poi_analysis").equals("1")) {
            try {
                System.out.println("loading GooglePoiService...");
                googlePoiService = new GooglePoiService(500, 800);
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            } catch (Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            }
        } else if(properties.getProperty("poi_analysis").equals("0")) {
            setModuleStatus(ModuleErrors.NOINTERNET, false);
        } else {
            System.out.println("failed to load GooglePoiService");
        }
    }

    private void initUpdateThread() {
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int iterations = 0;
                int lastExpression = 0;
                int lastCameraExecution = 0;
                int lastMapsExecution = 0;

                while (run) {
                    UserExpressions userExpressions = null;
                    if(userTracker.isUserTracked()) {
                        userExpressions = userTracker.getUserExpressions();
                        if(userExpressions != null) {
                            System.out.println("Expressions get updated");
                            setExpressionStatus(ExpressionType.KISS, userExpressions.isKiss());
                            setExpressionStatus(ExpressionType.MOUTHOPEN, userExpressions.isMouthOpen());
                            setExpressionStatus(ExpressionType.SMILE, userExpressions.isSmile());
                            setExpressionStatus(ExpressionType.TONGUEOUT, userExpressions.isTongueOut());
                        }
                        if(userExpressions != null && userExpressions.isKiss() && (iterations - lastExpression) >= 10) {
                            lastExpression = iterations;
                            if(cameraThread.getState() == Thread.State.NEW) {
                                lastCameraExecution = iterations;
                                cameraThread.start();
                            } else if(cameraThread.getState() == Thread.State.TERMINATED) {
                                lastCameraExecution = iterations;
                                initCameraThread();
                                cameraThread.start();
                            }
                        }
                    }
                    if((iterations - lastCameraExecution) >= 10) {
                        if(cameraThread.getState() == Thread.State.NEW) {
                            lastCameraExecution = iterations;
                            cameraThread.start();
                        } else if(cameraThread.getState() == Thread.State.TERMINATED) {
                            lastCameraExecution = iterations;
                            initCameraThread();
                            cameraThread.start();
                        }
                    }
                    if((iterations - lastMapsExecution) >= 10) {
                        if(mapsThread.getState() == Thread.State.NEW) {
                            lastMapsExecution = iterations;
                            mapsThread.start();
                        } else if(mapsThread.getState() == Thread.State.TERMINATED) {
                            lastMapsExecution = iterations;
                            initMapsThread();
                            mapsThread.start();
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cameraImage = SwingFXUtils.toFXImage(landscapeTracker.getImage(), null );
                                backgroundImage = new BackgroundImage(cameraImage,
                                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                                        BackgroundSize.DEFAULT);
                                background = new Background(backgroundImage);
                                backgroundProperty.setValue(background);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    iterations++;
                }
            }
        });
    }

    private void initMapsThread() {
        mapsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //GPS
                KinematicProperties kinematicProperties = null;
                try {
                    kinematicProperties = gpsTracker.fillDumpObject(kinematicProperties);
                    setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
                } catch (ModuleNotWorkingException e) {
                    setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
                    return;
                }
                if(kinematicProperties == null) {
                    return;
                }

                //POI maps
                List<PointOfInterest> pois = null;
                try{
                    List<GooglePoi> gPois = googlePoiService.loadPlaceForCircleAndPoiType(kinematicProperties,300
                            ,PoiType.FOOD/*GoogleType.zoo , GoogleType.airport, GoogleType.aquarium, GoogleType.church, GoogleType.city_hall,
                            GoogleType.hospital, GoogleType.library, GoogleType.mosque, GoogleType.museum, GoogleType.park,
                            GoogleType.stadium, GoogleType.synagogue, GoogleType.university,
                            GoogleType.point_of_interest, GoogleType.place_of_worship,
                            GoogleType.restaurant*/);
                    googlePoiService.downloadImages(gPois);
                    pois = (List) gPois;
                    setModuleStatus(ModuleErrors.NOINTERNET, true);
                } catch (Exception e){
                    e.printStackTrace();
                    setModuleStatus(ModuleErrors.NOINTERNET, false);
                    return;
                }
                if(pois == null || pois.size() == 0) {
                    return;
                }

                //Information Source
                /*try {
                    for (PointOfInterest poi: pois) {
                        poi = abstractProvider.provideAbstract(poi);
                    }
                    setModuleStatus(ModuleErrors.NOINTERNET, true);
                } catch (Exception e){
                    e.printStackTrace();
                    setModuleStatus(ModuleErrors.NOINTERNET, false);
                    return;
                }*/
                try {
                    for (PointOfInterest poi: pois) {
                        poi = knowledgeGraphSearch.getUrlById(poi);
                    }
                    setModuleStatus(ModuleErrors.NOINTERNET, true);
                } catch(ModuleNotWorkingException e) {
                    setModuleStatus(ModuleErrors.NOINTERNET, false);
                }

                for(PointOfInterest poi: pois) {
                    addPOImaps(poi);
                }
            }
        });
    }

    private void initCameraThread() {
        cameraThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Aufnahme Bild
                BufferedImage image = null;
                try {
                    image = landscapeTracker.getImage();
                    setModuleStatus(ModuleErrors.NOCAMERA, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    setModuleStatus(ModuleErrors.NOCAMERA, false);
                    return;
                }
                if (image == null) {
                    return;
                }

                //Analyse Bild
                List<PointOfInterest> pois = null;
                try {
                    pois = cloudVision.identifyPOIs(image);
                    setModuleStatus(ModuleErrors.NOINTERNET, true);
                } catch(Exception e) {
                    e.printStackTrace();
                    setModuleStatus(ModuleErrors.NOINTERNET, false);
                    return;
                }
                if (pois.isEmpty()) {
                    return;
                }

                //Abfrage Informationen
                /*try {
                    for (PointOfInterest poi: pois) {
                        poi = abstractProvider.provideAbstract(poi);
                    }
                    setModuleStatus(ModuleErrors.NOINTERNET, true);
                } catch(Exception e) {
                    e.printStackTrace();
                    setModuleStatus(ModuleErrors.NOINTERNET, false);
                    return;
                }*/
                try {
                    for (PointOfInterest poi: pois) {
                        poi = knowledgeGraphSearch.getUrlById(poi);
                    }
                    setModuleStatus(ModuleErrors.NOINTERNET, true);
                } catch(ModuleNotWorkingException e) {
                    setModuleStatus(ModuleErrors.NOINTERNET, false);
                }

                for (PointOfInterest poi: pois) {
                    addPOIcamera(poi);
                }
            }
        });
    }

    private void setModuleStatus(ModuleErrors type, boolean working) {
        for(ModuleStatusViewModel status: listModuleStatus) {
            if(status.getErrorType() == type) {
                status.setWorking(working);
                return;
            }
        }
        listModuleStatus.add(new ModuleStatusViewModel(type, working));
    }

    private void setExpressionStatus(ExpressionType type, boolean active) {
        for(UserExpressionViewModel expression: listExpressionStatus) {
            if(expression.getType() == type) {
                expression.setActive(active);
                return;
            }
        }
        listExpressionStatus.add(new UserExpressionViewModel(type, active));
    }

    private void addPOIcamera(PointOfInterest poi) {
        PoiViewModel item = convertPOI(poi);
        if(!propertyPOIcamera.contains(poi)) {
            pointsOfInterest.add(poi);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    propertyPOIcamera.add(item);
                }
            });

        }
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
        if(!propertyPOImaps.contains(poi)) {
            pointsOfInterest.add(poi);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    propertyPOImaps.add(item);
                }
            });
        }
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
        if(poi.getId() != null) {
            result.setId(poi.getId());
        }
        if(poi.getName() != null) {
            result.setName(poi.getName());
        }
        if(poi.getImage() != null) {
            result.setImage(SwingFXUtils.toFXImage(poi.getImage(), null ));
        }
        if(poi.getInformationAbstract() != null) {
            result.setInformationAbstract(poi.getInformationAbstract());
        }
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

    public ObservableList<ModuleStatusViewModel> getListModuleStatus() {
        return listModuleStatus.get();
    }

    public SimpleListProperty<ModuleStatusViewModel> listModuleStatusProperty() {
        return listModuleStatus;
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

    public ObservableList<UserExpressionViewModel> getListExpressionStatus() {
        return listExpressionStatus.get();
    }

    public SimpleListProperty<UserExpressionViewModel> listExpressionStatusProperty() {
        return listExpressionStatus;
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