package de.tuberlin.amos.ws17.swit.application.viewmodel;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.application.view.ApplicationView;
import de.tuberlin.amos.ws17.swit.application.view.ApplicationViewImplementation;
import de.tuberlin.amos.ws17.swit.common.*;
import de.tuberlin.amos.ws17.swit.common.Module;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.gps.GpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerImplementation;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerMock;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetectorMock;
import de.tuberlin.amos.ws17.swit.information_source.AbstractProvider;
import de.tuberlin.amos.ws17.swit.information_source.InformationProviderMock;
import de.tuberlin.amos.ws17.swit.landscape_tracking.DemoVideoLandscapeTracker;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTracker;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerImplementation;
import de.tuberlin.amos.ws17.swit.landscape_tracking.LandscapeTrackerMock;
import de.tuberlin.amos.ws17.swit.poi.MockedPoiService;
import de.tuberlin.amos.ws17.swit.poi.PoiService;
import de.tuberlin.amos.ws17.swit.poi.PoiType;
import de.tuberlin.amos.ws17.swit.poi.PoisInSightFinder;
import de.tuberlin.amos.ws17.swit.poi.google.GooglePoiService;
import de.tuberlin.amos.ws17.swit.tracking.JavoNetUserTracker;
import de.tuberlin.amos.ws17.swit.tracking.UserTracker;
import de.tuberlin.amos.ws17.swit.tracking.UserTrackerMock;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.apache.jena.atlas.logging.Log;
import org.joda.time.DateTime;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationViewModelImplementation implements ApplicationViewModel {

    private final static Logger LOGGER = Logger.getLogger(ApplicationViewModel.class.getName());

    //Module
    private ApplicationView  view;
    private LandmarkDetector cloudVision;
    private LandscapeTracker landscapeTracker;
    private UserTracker      userTracker;
    private GpsTracker       gpsTracker;
    private AbstractProvider abstractProvider;
    private PoiService       poiService;
    private PoisInSightFinder sightFinder = new PoisInSightFinder(300, 200, 200);

    //Threads
    private boolean isRunning;
    private Thread  updateThread;
    private Thread  cameraThread;
    private Thread  mapsThread;

    //Listen und Binding
    private List<PointOfInterest>                           pointsOfInterest     = new ArrayList<>();
    private PoiViewModel                                    expandedPOI          = new PoiViewModel();
    private UserPositionViewModel                           vmUserPosition       = new UserPositionViewModel();
    private SimpleListProperty<PoiViewModel>                propertyPoiMaps      = new SimpleListProperty<>();
    private SimpleListProperty<PoiViewModel>                propertyPoiCamera    = new SimpleListProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyCloseButton  = new SimpleObjectProperty<>();

    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleGpsButton  = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyTogglePoiButton  = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleUserTrackingButton  = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleLandscapeTrackingButton  = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleInformationSourceButton  = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleImageAnalysisButton  = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleApplicationViewButton  = new SimpleObjectProperty<>();

    private SimpleListProperty<String>                      propertyDebugLog     = new SimpleListProperty<>();
    private SimpleListProperty<ModuleStatusViewModel>       listModuleStatus     = new SimpleListProperty<>();
    private SimpleListProperty<UserExpressionViewModel>     listExpressionStatus = new SimpleListProperty<>();
    private List<Module>                                    moduleList           = new ArrayList<>();

    private int searchRadius = 1000;
    private GpsPosition lastRequestPosition;

    private Property<Image> propertyCameraImage = new SimpleObjectProperty<>();
    private Image cameraImage;
    public Property<Background> backgroundProperty = new SimpleObjectProperty<>();

    private BackgroundImage backgroundImage;
    private Background      background;

    private AppProperties properties = AppProperties.getInstance();

    public ApplicationViewModelImplementation(ApplicationViewImplementation view) {
        this.view = view;
        initObjects();
        initModules();

        isRunning = true;
        initUpdateThread();
        initMapsThread();
        initCameraThread();

        if (properties.useDebugLog) {
            initDebugLog();
        }

        updateBackgroundImage();

        updateThread.start();
    }

    private void initObjects() {
        listModuleStatus.set(FXCollections.observableList(new ArrayList<>()));
        listExpressionStatus.set(FXCollections.observableList(new ArrayList<>()));
        propertyPoiMaps.set(FXCollections.observableList(new ArrayList<>()));
        propertyPoiCamera.set(FXCollections.observableList(new ArrayList<>()));
        propertyDebugLog.set(FXCollections.observableList(new ArrayList<>()));
        propertyCloseButton.set(event -> minimizePoi());
        propertyToggleGpsButton.set(event -> DebugLog.toggleModule("GPS"));
        propertyTogglePoiButton.set(event -> DebugLog.toggleModule("POI"));
        propertyToggleUserTrackingButton.set(event -> DebugLog.toggleModule("UserTracking"));
        propertyToggleLandscapeTrackingButton.set(event -> DebugLog.toggleModule("LandscapeTracking"));
        propertyToggleImageAnalysisButton.set(event -> DebugLog.toggleModule("ImageAnalysis"));
        propertyToggleApplicationViewButton.set(event -> DebugLog.toggleModule("ApplicationView"));
        propertyToggleInformationSourceButton.set(event -> DebugLog.toggleModule("InformationSource"));
    }

    private void initDebugLog() {
        view.showDebugLog(true);
        System.out.println("loading DebugLog...");

        for (DebugLog.DebugEntry debugEntry : DebugLog.getDebugLog()) {
            propertyDebugLog.add(debugEntry.toString());
        }

        DebugLog.getDebugLog().addListener((ListChangeListener<DebugLog.DebugEntry>) c -> {
            c.next();
            propertyDebugLog.clear();
            for (DebugLog.DebugEntry de : c.getList()) {
                propertyDebugLog.add(de.toString());
            }
        });
    }

    private void initModules() {
        String currentModule;
        //GPS
        currentModule = "GpsTracker";
        if (properties.useGpsModule) {
            try {
                System.out.println("loading " + currentModule + "...");
                gpsTracker = new GpsTrackerImplementation();
                moduleList.add(gpsTracker);
                gpsTracker.startModule();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule);
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            }
        } else {
            try {
                System.out.println("loading " + currentModule + "Mock...");
                gpsTracker = new GpsTrackerMock();
                moduleList.add(gpsTracker);
                gpsTracker.startModule();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            }
        }

        //User Tracking
        currentModule = "UserTracker";
        if (properties.useIntelRealSense) {
            try {
                System.out.println("loading " + currentModule + "...");
                userTracker = new JavoNetUserTracker();
                userTracker.startTracking();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, true);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule);
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, false);
            }
        } else {
            try {
                System.out.println("loading " + currentModule + "Mock...");
                userTracker = new UserTrackerMock();
                userTracker.startTracking();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, true);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule + "Mock");
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOUSERCAMERA, false);
            }
        }

        //Landscape Tracking
        currentModule = "LandscapeTracker";
        if (properties.useExternalCamera) {
            try {
                System.out.println("loading " + currentModule + "...");
                landscapeTracker = new LandscapeTrackerImplementation();
                moduleList.add(landscapeTracker);
                landscapeTracker.startModule();
                setModuleStatus(ModuleErrors.NOCAMERA, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule);
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            }
        } else if (properties.useDemoVideo) {
            System.out.println("loading " + currentModule + "Demo...");
            landscapeTracker = new DemoVideoLandscapeTracker(view.getMediaView());
            moduleList.add(landscapeTracker);
            try {
                landscapeTracker.startModule();
            } catch (ModuleNotWorkingException e) {
                e.printStackTrace();
            }
            setModuleStatus(ModuleErrors.NOCAMERA, true);
        } else {
            try {
                System.out.println("loading " + currentModule + "Mock...");
                landscapeTracker = new LandscapeTrackerMock();
                moduleList.add(landscapeTracker);
                landscapeTracker.startModule();
                setModuleStatus(ModuleErrors.NOCAMERA, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule + "Mock");
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOCAMERA, false);
            }
        }

        //CloudVision
        currentModule = "LandmarkDetector";
        if (properties.useCloudVision) {
            try {
                System.out.println("loading " + currentModule + "...");
                cloudVision = CloudVision.getInstance();
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule);
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            }
        } else {
            System.out.println("loading " + currentModule + "Mock...");
            cloudVision = LandmarkDetectorMock.getInstance();
            setModuleStatus(ModuleErrors.NOINTERNET, true);
        }

        //Information Source
        currentModule = "AbstractProvider";
        if (properties.useWikipedia) {
            try {
                System.out.println("loading " + currentModule + "...");
                abstractProvider = AbstractProvider.getInstance();
                moduleList.add(abstractProvider);
                abstractProvider.startModule();
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule);
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            }
        } else {
            abstractProvider = new InformationProviderMock();
            System.out.println("loading " + currentModule + "Mock...");
            setModuleStatus(ModuleErrors.NOINTERNET, false);
        }

        //Google POI loader
        currentModule = "PoiService";
        if (properties.usePoiService) {
            try {
                System.out.println("loading " + currentModule + "...");
                //instantiate with the forbidden words from the properties file
                poiService = new GooglePoiService(500, 800,
                        Arrays.asList(properties.getProperty("places_to_ignore").split(",")));
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            } catch (Exception e) {
                System.out.println("unexpected error loading " + currentModule);
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
            }
        } else {
            System.out.println("loading " + currentModule + "Mock...");
            poiService = new MockedPoiService();
            setModuleStatus(ModuleErrors.NOINTERNET, true);
        }
    }

    private void initUpdateThread() {
        long startTime = new Date().getTime();
        updateThread = new Thread(() -> {
            //long iterations = 0;
            long lastExpression = startTime;
            //int lastCameraExecution = 0;
            long lastMapsExecution = startTime;
            while (isRunning) {
                long currentTime = new Date().getTime();
                long expressionTimeDiff = currentTime - lastExpression;
                long mapsTimeDiff = currentTime - lastMapsExecution;
                UserExpressions userExpressions;
                if (userTracker.isUserTracked()) {
                    setExpressionStatus(ExpressionType.ISRACKED, true);
                    userExpressions = userTracker.getUserExpressions();
                    if (userExpressions != null) {
                        setExpressionStatus(ExpressionType.KISS, userExpressions.isKiss());
                        setExpressionStatus(ExpressionType.MOUTHOPEN, userExpressions.isMouthOpen());
                        setExpressionStatus(ExpressionType.SMILE, userExpressions.isSmile());
                        setExpressionStatus(ExpressionType.TONGUEOUT, userExpressions.isTongueOut());
                    }
                    if (userExpressions != null && userExpressions.isKiss() && expressionTimeDiff >= 30) {
                        //System.out.println("ich mach expressions " + expressionTimeDiff);
                        if (cameraThread.getState() == Thread.State.NEW) {
                            lastExpression = currentTime;
                            cameraThread.start();
                        } else if (cameraThread.getState() == Thread.State.TERMINATED) {
                            lastExpression = currentTime;
                            initCameraThread();
                            cameraThread.start();
                        }
                    }
                } else {
                    setExpressionStatus(ExpressionType.ISRACKED, false);
                }
                if (mapsTimeDiff >= 30) {
                    //System.out.println("ich mach maps " + mapsTimeDiff);
                    if (mapsThread.getState() == Thread.State.NEW) {
                        lastMapsExecution = currentTime;
                        mapsThread.start();
                    } else if (mapsThread.getState() == Thread.State.TERMINATED) {
                        lastMapsExecution = currentTime;
                        initMapsThread();
                        mapsThread.start();
                    }
                }
                /*try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                updateBackgroundImage();

                //iterations++;
            }
        });
        updateThread.setDaemon(true);
    }

    @Override
    public void onKeyPressed(KeyCode code) {
        switch (code) {
            case F:
                analyzeImage();
                break;
            case D:
                view.toggleDebugLog();
                break;
            case L:
                view.toggleLists();
                break;
            default:
                break;
        }
    }

    private void initMapsThread() {
        mapsThread = new Thread(() -> {
            if (gpsTracker == null || poiService == null || abstractProvider == null) {
                System.out.println("unable to isRunning maps thread because of uninitialized modules");
                return;
            }

            //GPS
            KinematicProperties kinematicProperties = null;
            List<KinematicProperties> history = null;
            try {
                kinematicProperties = gpsTracker.fillDumpObject(kinematicProperties);
                history = gpsTracker.getGpsTrack(1);
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
                DateTime timeStamp = kinematicProperties.getTimeStamp();
                Double latitude = kinematicProperties.getLatitude();
                Double longitude = kinematicProperties.getLongitude();
                DebugLog.log("Lat: " +
                        latitude + ", Lng: " + longitude);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            } catch (Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
                return;
            }
            if (kinematicProperties == null) {
                System.out.println("Keine Position erfasst.");
                return;
            }

            int mapsPoisLoadDistance = properties.mapsPoisLoadDistance;
            if (lastRequestPosition == null || kinematicProperties.distanceTo(lastRequestPosition) > mapsPoisLoadDistance) {
                lastRequestPosition = new GpsPosition(kinematicProperties.getLongitude(), kinematicProperties.getLatitude());

                //POI maps
                Map<PointOfInterest, Float> pois;
                try {
                    List<PointOfInterest> poisFound = poiService.loadPlaceForCircleAndPoiType(kinematicProperties, searchRadius,
                            PoiType.LEISURE, PoiType.TOURISM);

                    pois = sightFinder.calculateDistances(kinematicProperties, poisFound);

                    //if there is a history: remove POIs out of viewrange
                    //or: no or irrelevant history
                    if (properties.getProperty("calculatePoisInSight").equals("1")
                            && history != null) {
                        //or: no or irrelevant history
                        if (!history.isEmpty()) {
                            GpsPosition historyPoint = history.get(0);
                            if (historyPoint.distanceTo(kinematicProperties) > 0.5) {
                                System.out.println("Point in histroy found. Size of POIs now: " + pois.size());
                                pois = sightFinder.getPoisInViewAngle(historyPoint, kinematicProperties, pois.keySet());
                                System.out.println("Used viewrange calculation now pois are of size: " + pois.size());
                            }
                        }
                    }

                    System.out.println(pois.size() + " POIs found.");

                    clearDuplicates(pois.keySet(), "map");

                    System.out.println(pois.size() + "NEW POIs found.");

                    if (properties.getProperty("load_images").equals("1")) {
                        poiService.addImages(pois.keySet());
                        System.out.println(pois.size() + " images added.");
                    } else
                        System.out.println("Image download is turned of.");

                    setModuleStatus(ModuleErrors.NOINTERNET, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    setModuleStatus(ModuleErrors.NOINTERNET, false);
                    return;
                }

                if (pois.isEmpty()) {
                    return;
                }

                //retrieve information
                getAbstract(new ArrayList<>(pois.keySet()));

                for (PointOfInterest poi : pois.keySet()) {
                    addMapsPoi(poi);
                }
            }
        });
        // thread will not prevent application shutdown
        mapsThread.setDaemon(true);
    }

    private void getAbstract(List<PointOfInterest> pois) {
        try {
            for (PointOfInterest poi : pois) {
                abstractProvider.setInfoAndUrl(poi);
            }
            setModuleStatus(ModuleErrors.NOINTERNET, true);
        } catch (Exception e) {
            setModuleStatus(ModuleErrors.NOINTERNET, false);
        }
    }

    public void analyzeImage() {
        BufferedImage image;
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
        List<PointOfInterest> pois;
        try {
            pois = cloudVision.identifyPOIs(image);
            setModuleStatus(ModuleErrors.NOINTERNET, true);
        } catch (Exception e) {
            e.printStackTrace();
            setModuleStatus(ModuleErrors.NOINTERNET, false);
            return;
        }

        if (pois.isEmpty()) {
            return;
        }
        clearDuplicates(pois, "camera");

        getAbstract(pois);

        for (PointOfInterest poi : pois) {
            addCameraPoi(poi);
        }
    }

    private void initCameraThread() {
        cameraThread = new Thread(() -> {

            if (landscapeTracker == null || cloudVision == null || abstractProvider == null) {
                System.out.println("unable to isRunning camera thread because of uninitialized modules");
                return;
            }

            if (properties.useDemoVideo) {
                Platform.runLater(this::analyzeImage);
            } else {
                analyzeImage();
            }
        });

        // thread will not prevent application shutdown
        cameraThread.setDaemon(true);
    }

    private void clearDuplicates(Collection<PointOfInterest> pois, String propertyList) {
        ArrayList<PointOfInterest> list = new ArrayList<>();
        for (PointOfInterest poi : pois) {
            PoiViewModel item = convertPoi(poi);
            if (propertyList.equals("map")) {
                if (propertyPoiMaps.contains(item)) {
                    list.add(poi);
                }
            } else if (propertyList.equals("camera")) {
                if (propertyPoiCamera.contains(item)) {
                    list.add(poi);
                }
            }
        }
        pois.removeAll(list);

    }


    private void setModuleStatus(ModuleErrors type, boolean working) {
        for (ModuleStatusViewModel status : listModuleStatus) {
            if (status.getErrorType() == type) {
                Platform.runLater(() -> status.setWorking(working));
                return;
            }
        }
        listModuleStatus.add(new ModuleStatusViewModel(type, working));
    }

    private void setExpressionStatus(ExpressionType type, boolean active) {
        for (UserExpressionViewModel expression : listExpressionStatus) {
            if (expression.getType() == type) {
                Platform.runLater(() -> expression.setActive(active));
                return;
            }
        }
        listExpressionStatus.add(new UserExpressionViewModel(type, active));
    }

    private void updateBackgroundImage() {

        Platform.runLater(() -> {
            try {
                if (landscapeTracker != null) {
                    cameraImage = SwingFXUtils.toFXImage(landscapeTracker.getImage(), null);
                    backgroundImage = new BackgroundImage(cameraImage,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(100, 100, true, true, false, true));
                    background = new Background(backgroundImage);
                    backgroundProperty.setValue(background);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addCameraPoi(PointOfInterest poi) {
        PoiViewModel item = convertPoi(poi);
        if (!propertyPoiCamera.contains(poi)) {
            pointsOfInterest.add(poi);
            Platform.runLater(() -> propertyPoiCamera.add(0, item));
        }
    }

    private boolean removeCameraPoi(String id) {
        for (PoiViewModel item : propertyPoiCamera) {
            if (item.getId().equals(id)) {
                propertyPoiCamera.remove(item);
                return true;
            }
        }
        return false;
    }

    private void addMapsPoi(PointOfInterest poi) {
        PoiViewModel item = convertPoi(poi);
        if (!propertyPoiMaps.contains(poi)) {
            pointsOfInterest.add(poi);
            Platform.runLater(() -> propertyPoiMaps.add(0, item));
        }
    }

    private boolean removeMapsPoi(String id) {
        for (PoiViewModel item : propertyPoiMaps) {
            if (item.getId().equals(id)) {
                propertyPoiMaps.remove(item);
                return true;
            }
        }
        return false;
    }

    private PoiViewModel convertPoi(PointOfInterest poi) {
        PoiViewModel result = new PoiViewModel();
        if (poi.getId() != null) {
            result.setId(poi.getId());
        }
        if (poi.getName() != null) {
            result.setName(poi.getName());
        }
        if (poi.getImage() != null) {
            result.setImage(SwingFXUtils.toFXImage(poi.getImage(), null));
        }
        if (poi.getInformationAbstract() != null) {
            result.setInformationAbstract(poi.getInformationAbstract());
        }
        return result;
    }

    @Override
    public boolean expandPoi(String id) {
        for (PoiViewModel item : propertyPoiCamera) {
            if (item.getId().equals(id)) {
                setExpandedPoi(item);
                return true;
            }
        }
        for (PoiViewModel item : propertyPoiMaps) {
            if (item.getId().equals(id)) {
                setExpandedPoi(item);
                return true;
            }
        }
        return false;
    }

    private void minimizePoi() {
        expandedPOI.setId("");
        expandedPOI.setName("");
        expandedPOI.setImage(null);
        expandedPOI.setInformationAbstract("");
        view.showExpandedPoi(false);
    }

    private void setExpandedPoi(PoiViewModel item) {
        expandedPOI.setId(item.getId());
        expandedPOI.setName(item.getName());
        expandedPOI.setImage(item.getImage());
        expandedPOI.setInformationAbstract(item.getInformationAbstract());
        view.showExpandedPoi(true);
    }

    @Override
    public PoiViewModel getExpandedPOI() {
        return expandedPOI;
    }

    @Override
    public SimpleListProperty<PoiViewModel> propertyPoiMapsProperty() {
        return propertyPoiMaps;
    }

    @Override
    public SimpleListProperty<PoiViewModel> propertyPoiCameraProperty() {
        return propertyPoiCamera;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyCloseButtonProperty() {
        return propertyCloseButton;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleGpsButtonProperty() { return propertyToggleGpsButton; }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyTogglePoiButtonProperty() { return propertyTogglePoiButton; }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleUserTrackingButtonProperty() { return propertyToggleUserTrackingButton; }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleLandscapeTrackingButtonProperty() { return propertyToggleLandscapeTrackingButton; }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleInformationSourceButtonProperty() { return propertyToggleInformationSourceButton; }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleApplicationViewButtonProperty() { return propertyToggleApplicationViewButton; }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleImageAnalysisButtonProperty() { return propertyToggleImageAnalysisButton; }

    @Override
    public SimpleListProperty<String> propertyDebugLogProperty() {
        return propertyDebugLog;
    }

    @Override
    public Property<Background> getBackgroundProperty() {
        return backgroundProperty;
    }

    @Override
    public SimpleListProperty<ModuleStatusViewModel> listModuleStatusProperty() {
        return listModuleStatus;
    }

    @Override
    public List<Module> getModuleList() {
        return moduleList;
    }

    @Override
    public void setRunning(boolean running) {

    }

    public SimpleListProperty<UserExpressionViewModel> listExpressionStatusProperty() {
        return listExpressionStatus;
    }

    public Property<Image> propertyCameraImageProperty() {
        return propertyCameraImage;
    }
}
