package de.tuberlin.amos.ws17.swit.application.viewmodel;

import de.tuberlin.amos.ws17.swit.application.AppProperties;
import de.tuberlin.amos.ws17.swit.application.view.ApplicationView;
import de.tuberlin.amos.ws17.swit.application.view.ApplicationViewImplementation;
import de.tuberlin.amos.ws17.swit.common.*;
import de.tuberlin.amos.ws17.swit.common.exceptions.ModuleNotWorkingException;
import de.tuberlin.amos.ws17.swit.gps.DemoVideoGpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTracker;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerImplementation;
import de.tuberlin.amos.ws17.swit.gps.GpsTrackerMock;
import de.tuberlin.amos.ws17.swit.image_analysis.CloudVision;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetector;
import de.tuberlin.amos.ws17.swit.image_analysis.LandmarkDetectorMock;
import de.tuberlin.amos.ws17.swit.image_analysis.TFLandmarkClassifier;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ApplicationViewModelImplementation implements ApplicationViewModel {

    public static final  String videoFileName       = "amos.mp4";
    private static final int    HIDE_INFO_BOX_DELAY = 10000; // 10 seconds

    //Module
    private ApplicationView      view;
    private LandmarkDetector     cloudVision;
    private LandscapeTracker     landscapeTracker;
    private TFLandmarkClassifier tensorFlowClassifier;
    private UserTracker          userTracker;
    private GpsTracker           gpsTracker;
    private AbstractProvider     abstractProvider;
    private PoiService           poiService;
    private PoisInSightFinder sightFinder = new PoisInSightFinder(300, 200, 200);

    //Threads
    private boolean isRunning;
    private Thread  updateThread;
    private Thread  cameraThread;
    private Thread  mapsThread;

    //Listen und Binding
    private List<PointOfInterest>                           pointsOfInterest    = new ArrayList<>();
    private PoiViewModel                                    expandedPOI         = new PoiViewModel();
    private UserPositionViewModel                           vmUserPosition      = new UserPositionViewModel();
    private SimpleListProperty<PoiViewModel>                propertyPoiMaps     = new SimpleListProperty<>();
    private SimpleListProperty<PoiViewModel>                propertyPoiCamera   = new SimpleListProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyCloseButton = new SimpleObjectProperty<>();

    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleGpsButton               = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyTogglePoiButton               = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleUserTrackingButton      = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleLandscapeTrackingButton = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleInformationSourceButton = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleImageAnalysisButton     = new SimpleObjectProperty<>();
    private SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleApplicationViewButton   = new SimpleObjectProperty<>();

    private SimpleListProperty<String>                  propertyDebugLog     = new SimpleListProperty<>();
    private SimpleListProperty<ModuleStatusViewModel>   listModuleStatus     = new SimpleListProperty<>();
    private SimpleListProperty<UserExpressionViewModel> listExpressionStatus = new SimpleListProperty<>();


    private SimpleDoubleProperty infoBoxRotation     = new SimpleDoubleProperty();
    private SimpleDoubleProperty infoBoxTranslationX = new SimpleDoubleProperty();
    private SimpleDoubleProperty infoBoxTranslationY = new SimpleDoubleProperty();

    private SimpleBooleanProperty debugLayerVisible       = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty applicationLayerVisible = new SimpleBooleanProperty(false);

    private int searchRadius = 1000;
    private GpsPosition lastRequestPosition;
    private List<Module> moduleList = new ArrayList<>();

    private Property<Image> propertyCameraImage = new SimpleObjectProperty<>();
    private Image cameraImage;
    private Property<Background> backgroundProperty = new SimpleObjectProperty<>();

    private BackgroundImage backgroundImage;
    private Background      background;
    private ScheduledExecutorService tensorFlowScheduler  = Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService hideInfoBoxScheduler = Executors.newSingleThreadScheduledExecutor();
    private AppProperties            properties           = AppProperties.getInstance();

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
        if (properties.useTensorflow) {
            initTFClassifier();
        }

        initInfoBoxMovement();

        debugLayerVisible.addListener(event -> {
            if (properties.useAnimations) {
                Platform.runLater(() -> {
                    if (debugLayerVisible.get()) {
                        view.showDebugLayer();
                    } else {
                        view.hideDebugLayer();
                    }
                });
            }
        });
        applicationLayerVisible.addListener(event -> {
            if (properties.useAnimations) {
                Platform.runLater(() -> {
                    if (applicationLayerVisible.get()) {
                        view.showApplicationLayer();
                    } else {
                        view.hideApplicationLayer();
                    }
                });
            }
        });
    }

    private void initTFClassifier() {
        view.toggleTensorFlowDebugWindow();
        int intervalInMillis = 5000;
        if (properties.useDemoVideo) {
            // grab image every second if using video
            intervalInMillis = 1000;
        }
        tensorFlowScheduler.scheduleAtFixedRate(() -> {
            BufferedImage image = getLandscapeTrackerImage();
            if (image != null) {
                if (gpsTracker != null) {
                    tensorFlowClassifier.identifyPOIs(image, gpsTracker.getCurrentPosition());
                } else {
                    tensorFlowClassifier.identifyPOIs(image);
                }
            }
        }, 1000, intervalInMillis, TimeUnit.MILLISECONDS);
    }

    private void initObjects() {
        listModuleStatus.set(FXCollections.observableList(new ArrayList<>()));
        listExpressionStatus.set(FXCollections.observableList(new ArrayList<>()));
        propertyPoiMaps.set(FXCollections.observableList(new ArrayList<>()));
        propertyPoiCamera.set(FXCollections.observableList(new ArrayList<>()));
        propertyDebugLog.set(FXCollections.observableList(new ArrayList<>()));
        propertyCloseButton.set(event -> minimizePoi());
        propertyToggleGpsButton.set(event -> DebugLog.toggleModule(DebugLog.gps));
        propertyTogglePoiButton.set(event -> DebugLog.toggleModule(DebugLog.poi));
        propertyToggleUserTrackingButton.set(event -> DebugLog.toggleModule(DebugLog.userTracking));
        propertyToggleLandscapeTrackingButton.set(event -> DebugLog.toggleModule(DebugLog.landscapeTracking));
        propertyToggleImageAnalysisButton.set(event -> DebugLog.toggleModule(DebugLog.imageAnalysis));
        propertyToggleApplicationViewButton.set(event -> DebugLog.toggleModule(DebugLog.applicationView));
        propertyToggleInformationSourceButton.set(event -> DebugLog.toggleModule(DebugLog.informationSource));

        debugEntries.set(DebugLog.getDebugLog());
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
        if (!properties.useDemoVideo) {
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
        } else {
            try {
                System.out.println("loading " + currentModule + " for demo video...");
                gpsTracker = new DemoVideoGpsTracker();
                moduleList.add(gpsTracker);
                gpsTracker.startModule();
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
            } catch (IOException e) {
                System.out.println("could not load json file " + currentModule);
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
            landscapeTracker = new DemoVideoLandscapeTracker(view.getMediaView(), videoFileName);
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

        if (properties.useTensorflow) {
            try {
                System.out.println("loading Tensorflow model");
                tensorFlowClassifier = new TFLandmarkClassifier();
            } catch (IOException e) {
                System.out.println("Failed to load Tensorflow model graph");
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
            long lastMouthOpen = startTime;
            long lastSmile = startTime;
            long lastTongueOut = startTime;
            while (isRunning) {
                long currentTime = new Date().getTime();
                long expressionTimeDiff = (currentTime - lastExpression) / 1000;
                long mapsTimeDiff = (currentTime - lastMapsExecution) / 1000;
                long mouthOpenDiff = (currentTime - lastMouthOpen) / 1000;
                long smileDiff = (currentTime - lastSmile) / 1000;
                long tongueOutDiff = (currentTime - lastTongueOut) / 1000;
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
                    if (userExpressions != null && userExpressions.isKiss() && expressionTimeDiff >= 5) {
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
                    if (userExpressions != null && userExpressions.isMouthOpen() && mouthOpenDiff >= 5) {
                        applicationLayerVisible.set(!applicationLayerVisible.get());
                        lastMouthOpen = currentTime;
                    }
                    if (userExpressions != null && userExpressions.isSmile() && smileDiff >= 5) {

                        lastSmile = currentTime;
                    }
                    if (userExpressions != null && userExpressions.isTongueOut() && tongueOutDiff >= 5) {
                        debugLayerVisible.set(!debugLayerVisible.get());
                        lastTongueOut = currentTime;
                    }
                } else {
                    debugLayerVisible.set(false);
                    applicationLayerVisible.set(false);
                    setExpressionStatus(ExpressionType.ISRACKED, false);
                }
                if (mapsTimeDiff >= 5) {
                    lastMapsExecution = currentTime;
                    if (mapsThread.getState() == Thread.State.NEW) {
                        mapsThread.start();
                    } else if (mapsThread.getState() == Thread.State.TERMINATED) {
                        initMapsThread();
                        mapsThread.start();
                    }
                }
                try {
                    Thread.sleep(125);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                updateBackgroundImage();
                updateInfoBox();
                //iterations++;
            }
        });
        updateThread.setDaemon(true);
    }

    @Override
    public void onKeyPressed(KeyCode code) {
        switch (code) {
            case F:
                new Thread(this::analyzeImage).start();
                break;
            case D:
                view.toggleDebugLog();
                break;
            case L:
                view.toggleLists();
                break;
            case T:
                view.toggleTensorFlowDebugWindow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onInfoTextScrolled() {
        hideInfoBoxScheduler.shutdown();
    }

    private void initMapsThread() {
        mapsThread = new Thread(() -> {
            if (gpsTracker == null || poiService == null || abstractProvider == null) {
                System.out.println("unable to isRunning maps thread because of uninitialized modules");
                return;
            }

            //GPS
            KinematicProperties kinematicProperties = new KinematicProperties();
            List<KinematicProperties> history = null;
            try {
                kinematicProperties = gpsTracker.fillDumpObject(kinematicProperties);
                history = gpsTracker.getGpsTrack(1);
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, true);
                DateTime timeStamp = kinematicProperties.getTimeStamp();
                Double latitude = kinematicProperties.getLatitude();
                Double longitude = kinematicProperties.getLongitude();
                DebugLog.log(DebugLog.SOURCE_GPS, "Current Position: (" +
                        latitude + ", " + longitude + ")");
            } catch (ModuleNotWorkingException e) {
                setModuleStatus(ModuleErrors.NOGPSHARDWARE, false);
                kinematicProperties = null;
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

    private BufferedImage getLandscapeTrackerImage() {
        BufferedImage image = null;
        try {
            image = landscapeTracker.getImage();
            setModuleStatus(ModuleErrors.NOCAMERA, true);
        } catch (Exception e) {
            e.printStackTrace();
            setModuleStatus(ModuleErrors.NOCAMERA, false);
        }
        return image;
    }

    private void analyzeImage() {
        BufferedImage image = getLandscapeTrackerImage();
        if (image == null) {
            return;
        }

        List<PointOfInterest> pois = new ArrayList<>();

        // try local classification first
        if (tensorFlowClassifier != null) {
            pois = tensorFlowClassifier.identifyPOIs(image, gpsTracker.getCurrentPosition());
        }

        // if not successful -> use cloud vision
        if (pois.isEmpty()) {
            try {
                pois = cloudVision.identifyPOIs(image);
                setModuleStatus(ModuleErrors.NOINTERNET, true);
            } catch (Exception e) {
                e.printStackTrace();
                setModuleStatus(ModuleErrors.NOINTERNET, false);
                return;
            }
        }

        getAbstract(pois);

        for (PointOfInterest poi : pois) {
            PoiViewModel item = convertPoi(poi);
            if (!propertyPoiCamera.contains(item)) {
                addCameraPoi(poi);
            }
        }
    }

    private void initCameraThread() {
        cameraThread = new Thread(() -> {

            if (landscapeTracker == null || cloudVision == null || abstractProvider == null) {
                System.out.println("unable to isRunning camera thread because of uninitialized modules");
                return;
            }

            analyzeImage();
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
        try {
            pois.removeAll(list);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }

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
        if (AppProperties.getInstance().useDemoVideo) {
            return;
        }
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

    //private double infoBoxMoveWidth = 300;
    private double infoboxMoveMaxX = 0;
    private double infoboxMoveMaxY = 0;

    private double infoboxMoveWidthFactor  = 1;
    private double infoboxMoveHeightFactor = 1;

    private double userPositionMinY = 0;
    private double userPositionMaxY = 0;


    private void initInfoBoxMovement() {
        Screen screen = Screen.getPrimary();
        Rectangle2D screenVisualBounds = screen.getVisualBounds();

        infoboxMoveMaxX = screenVisualBounds.getWidth() * 0.35;
        infoboxMoveMaxY = screenVisualBounds.getHeight() * 0.175;
        infoboxMoveWidthFactor = infoboxMoveMaxX / 150;
        infoboxMoveHeightFactor = infoboxMoveMaxY / 75;
    }

    private void updateInfoBox() {
        if (userTracker.isUserTracked()) {
            UserPosition userPosition = userTracker.getUserPosition();
            double headCenterX = Math.max(Math.min(userPosition.getHeadCenterPosition().getX(), 150), -150);
            double headCenterY = Math.max(Math.min(userPosition.getHeadCenterPosition().getY(), 100), -100);
            //double headCenterY = Math.max(userPosition.getHeadCenterPosition().getY(), 150);

            infoBoxTranslationX.setValue(-headCenterX * infoboxMoveWidthFactor);
            infoBoxTranslationY.setValue(-headCenterY * infoboxMoveHeightFactor);
            infoBoxRotation.setValue(userPosition.getLineOfSight().getX() / 1.5);

            if (userPositionMaxY < userPosition.getHeadCenterPosition().getY()) {
                userPositionMaxY = userPosition.getHeadCenterPosition().getY();
                System.out.println("userPositionMaxY " + userPositionMaxY);
                System.out.println("userPositionMinY " + userPositionMinY);
            }

            if (userPositionMinY > userPosition.getHeadCenterPosition().getY()) {
                userPositionMinY = userPosition.getHeadCenterPosition().getY();
                System.out.println("userPositionMaxY " + userPositionMaxY);
                System.out.println("userPositionMinY " + userPositionMinY);
            }
        } else {
            infoBoxTranslationX.setValue(0);
            infoBoxTranslationY.setValue(0);
            infoBoxRotation.setValue(0);
        }
    }

    private void addCameraPoi(PointOfInterest poi) {
        PoiViewModel item = convertPoi(poi);
        if (!propertyPoiCamera.contains(poi)) {
            pointsOfInterest.add(poi);
            Platform.runLater(() -> {
                propertyPoiCamera.add(0, item);
                // only show if info box is not filled
                if (expandedPOI == null || StringUtils.isEmpty(expandedPOI.getName())) {
                    setExpandedPoi(item);
                    view.showExpandedPoi(true);
                    view.showInfoBoxHideIndicator(HIDE_INFO_BOX_DELAY);
                    hideInfoBoxScheduler = Executors.newSingleThreadScheduledExecutor();
                    hideInfoBoxScheduler.scheduleAtFixedRate(this::minimizePoi, HIDE_INFO_BOX_DELAY, 1000, TimeUnit.MILLISECONDS);
                }
            });
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
        view.showExpandedPoi(false);
        hideInfoBoxScheduler.shutdown();
        expandedPOI.setName("");
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
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleGpsButtonProperty() {
        return propertyToggleGpsButton;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyTogglePoiButtonProperty() {
        return propertyTogglePoiButton;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleUserTrackingButtonProperty() {
        return propertyToggleUserTrackingButton;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleLandscapeTrackingButtonProperty() {
        return propertyToggleLandscapeTrackingButton;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleInformationSourceButtonProperty() {
        return propertyToggleInformationSourceButton;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleApplicationViewButtonProperty() {
        return propertyToggleApplicationViewButton;
    }

    @Override
    public SimpleObjectProperty<EventHandler<ActionEvent>> propertyToggleImageAnalysisButtonProperty() {
        return propertyToggleImageAnalysisButton;
    }

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
        if (!running) {
            tensorFlowScheduler.shutdown();
        }
    }

    @Override
    public SimpleDoubleProperty getInfoBoxRotation() {
        return infoBoxRotation;
    }

    @Override
    public SimpleDoubleProperty getInfoBoxTranslationX() {
        return infoBoxTranslationX;
    }

    @Override
    public SimpleDoubleProperty getInfoBoxTranslationY() {
        return infoBoxTranslationY;
    }

    private SimpleListProperty<DebugLog.DebugEntry> debugEntries = new SimpleListProperty<>();

    public SimpleListProperty<DebugLog.DebugEntry> propertyDebugEntries() {
        return debugEntries;
    }

    public SimpleListProperty<UserExpressionViewModel> listExpressionStatusProperty() {
        return listExpressionStatus;
    }

    public Property<Image> propertyCameraImageProperty() {
        return propertyCameraImage;
    }
}
