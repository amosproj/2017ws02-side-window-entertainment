//package de.tuberlin.amos.ws17.swit.tracking;
//
//import de.tuberlin.amos.ws17.swit.common.UserExpressions;
//import de.tuberlin.amos.ws17.swit.common.UserPosition;
//import de.tuberlin.amos.ws17.swit.common.Vector3D;
//import de.tuberlin.amos.ws17.swit.tracking.camera.*;
//
//import intel.rssdk.*;
//
//public class RealSenseSdkUserTracker implements UserTracker, Runnable {
//
//    private boolean isUserTracked;
//    @Override
//    public boolean getIsUserTracked() {
//        return isUserTracked;
//    }
//
//    private UserPosition userPosition;
//    //@Override
//    public UserPosition getUsererPosition() {
//        return userPosition;
//    }
//
//    private UserExpressions userExpressions;
//    @Override
//    public UserExpressions getUserExpressions() {
//        return userExpressions;
//    }
//
//    private boolean isTracking = false;
//    private Thread trackingThread;
//
//
//    private CameraService cameraService;
//    //private Camera intelRealSenseCamera;
//    //private Resolution_ColorDepth_FrameRateCombination selectedResolution_ColorDepth_FrameRateCombination;
//
//
//    //private PXCMCaptureManager captureManager;
//
//    //private PXCMSession session;
//    private PXCMSenseManager senseManager;
//    private PXCMFaceModule faceModule;
//    private PXCMFaceConfiguration faceConfiguration;
//    private PXCMFaceConfiguration.ExpressionsConfiguration expressionsConfiguration;
//
//    public RealSenseSdkUserTracker() {
//
//    }
//
//    @Override
//    public boolean startTracking() throws Exception {
//        stopTracking();
//
//        initializeRealSenseSdk();
//        selectHardwareAndStreamProfile();
//        setFaceConfiguration();
//        initSenseManager();
//
//        startTrackingThread();
//        return true;
//    }
//
//    @Override
//    public boolean stopTracking() {
//        stopTrackingThread();
//
//        return false;
//    }
//
//    private void startTrackingThread() {
//        isTracking = true;
//        trackingThread = new Thread(this);
//        trackingThread.start();
//    }
//
//    private void stopTrackingThread() {
//        if (isTracking) {
//            isTracking = false;
//
//            while (trackingThread != null && trackingThread.isAlive()) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            trackingThread = null;
//        }
//    }
//
//    private void initializeRealSenseSdk() throws Exception {
//        //session = PXCMSession.CreateInstance();
//        //if (session == null) {
//        //    throw new Exception("PXCMSession.CreateInstance() failed.");
//        //}
//
//        senseManager = PXCMSenseManager.CreateInstance();// session.CreateSenseManager();
//        if (senseManager == null) {
//            throw new Exception("session.CreateSenseManager() failed.");
//        }
//
//        cameraService = new JavoNetCameraService();
//        cameraService.loadCameras();
//    }
//
//    private void finalizeRealSenseSdk() {
//        if (faceConfiguration != null) {
//            faceConfiguration.close();
//            faceConfiguration = null;
//        }
//
//        if (senseManager != null) {
//            senseManager.close();
//            senseManager = null;
//        }
//
//        //if (cameraService != null) {
//        //    cameraService = null;
//        //}
//
//        //if (session != null) {
//        //    session.close();
//        //    session = null;
//        //}
//    }
//
//    private void selectHardwareAndStreamProfile() throws Exception {
//        cameraService.selectUserTrackingCamera();
//        //intelRealSenseCamera = cameraService.GetUserTrackingCamera();
//        //if (intelRealSenseCamera == null)
//        //    throw new Exception("Intel RealSense Camera not connected!");
//
//        //selectedResolution_ColorDepth_FrameRateCombination = cameraService.getResolution_ColorDepth_FrameRateCombination(intelRealSenseCamera);
//        //if (selectedResolution_ColorDepth_FrameRateCombination == null)
//        //    throw new Exception("Resolution_ColorDepth_FrameRateCombination not supported");
//
//
//        //captureManager = senseManager..captureManager;
//        //captureManager.FilterByDeviceInfo(intelRealSenseCamera.device.deviceInfo);
//
////        PXCMCapture.Device.StreamProfileSet streamFrofileSet = new PXCMCapture.Device.StreamProfileSet();
////        streamFrofileSet
////        {
////            color =
////                {
////                    frameRate = SelectedResolution_ColorDepth_FrameRateCombination.FrameRate,
////                    imageInfo =
////                        {
////                            format = SelectedResolution_ColorDepth_FrameRateCombination.PixelFormat,
////                            height = SelectedResolution_ColorDepth_FrameRateCombination.Height,
////                            width = SelectedResolution_ColorDepth_FrameRateCombination.Width
////                        }
////                }
////        };
////
////        captureManager.FilterByStreamProfiles(streamFrofileSet);
//    }
//
//    private void setFaceConfiguration() throws Exception {
//        //TODO: Parameter?
//        pxcmStatus enableFaceStatus = senseManager.EnableFace(null);// senseManager.EnableFace(null);
//        if (enableFaceStatus.isError()) {
//            throw new Exception("senseManager.EnableFace() failed.");
//        }
//
//        faceModule = senseManager.QueryFace();
//        if (faceModule == null) {
//            throw new Exception("senseManager.QueryFace() failed");
//        }
//
//        faceConfiguration = faceModule.CreateActiveConfiguration();
//        if (faceConfiguration == null) {
//            throw new Exception("faceModule.CreateActiveConfiguration() failed");
//        }
//
//        faceConfiguration.SetTrackingMode(PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR_PLUS_DEPTH);
//        faceConfiguration.strategy = PXCMFaceConfiguration.TrackingStrategyType.STRATEGY_CLOSEST_TO_FARTHEST;
//
//        //Detection
//        //faceConfiguration.detection.isEnabled = false;
//        //faceConfiguration.detection.maxTrackedFaces = 1;
//
//        //Landmarks
//        //faceConfiguration.landmarks.isEnabled = true;
//        //faceConfiguration.landmarks.maxTrackedFaces = 1;
//        //faceConfiguration.landmarks.smoothingLevel = PXCMFaceConfiguration.SmoothingLevelType.SMOOTHING_DISABLED;
//
//        //Configuration of Pose
//        faceConfiguration.pose.isEnabled = true;
//        faceConfiguration.pose.maxTrackedFaces = 1;
//        faceConfiguration.pose.smoothingLevel = PXCMFaceConfiguration.SmoothingLevelType.SMOOTHING_DISABLED;
//
//        //Configuration of Gaze
//
//        //Configuration of Expressions
//        expressionsConfiguration = faceConfiguration.QueryExpressions();
//        if (expressionsConfiguration == null) {
//            throw new Exception("faceConfiguration.QueryExpressions() failed");
//        }
//        //expressionsConfiguration.EnableAllExpressions();
//        expressionsConfiguration.properties.isEnabled = true;
//        expressionsConfiguration.properties.maxTrackedFaces = 1;
//        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS);
//        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT);
//        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE);
//        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN);
//        //TODO ist das überhaupt nötig?
//        //faceConfiguration.EnableAllAlerts();
//        //faceConfiguration.SubscribeAlert(OnAlert);
//
//        pxcmStatus applyChangesStatus = faceConfiguration.ApplyChanges();
//        //TODO: unter Java einfach mal ignorieren
//        //if (applyChangesStatus != pxcmStatus.PXCM_STATUS_NO_ERROR)
//        //    throw new Exception("FaceConfiguration.ApplyChanges() error: " + applyChangesStatus.toString());
//    }
//
//    private void initSenseManager() throws Exception {
//        pxcmStatus senseManagerInitStatus = senseManager.Init();
//        if (senseManagerInitStatus != pxcmStatus.PXCM_STATUS_NO_ERROR) {
//            throw new Exception("SenseManager.Init() error: " + senseManagerInitStatus.toString());
//        }
//    }
//
//    @Override
//    public void run() {
//        PXCMFaceData faceData = faceModule.CreateOutput();
//
//        while (isTracking) {
//
//            pxcmStatus acquireFrameStatus = senseManager.AcquireFrame(true);
//            if (acquireFrameStatus != pxcmStatus.PXCM_STATUS_NO_ERROR) {
//                resetTracking();
//                //System.out.println("SenseManager.AcquireFrame(true) error: " + acquireFrameStatus.toString());
//                continue;
//            }
//            //TODO: Image Daten holen aber erstmal CaptureManager verstehen auf java Seite
////            PXCMCapture.Sample captureSample = senseManager.QueryFaceSample();
////            if (captureSample == null)
////            {
////                resetTracking();
////                senseManager.ReleaseFrame();
////                continue;
////            }
//            //
//            //TrackImageData(captureSample);
//
//            faceData.Update();
//
//            int numberOfDetectedFaces = faceData.QueryNumberOfDetectedFaces();
//            if (numberOfDetectedFaces != 1) {
//                resetTracking();
//                senseManager.ReleaseFrame();
//                continue;
//            }
//
//            PXCMFaceData.Face faceDataFace = faceData.QueryFaceByIndex(0);
//
//            trackPose(faceDataFace);
//            trackExpressions(faceDataFace);
//            //TrackLandmarks(faceDataFace);
//            //TrackGaze();
//
//
//            //FaceData.QueryRecognitionModule();
//
//            //im nächsten object steckt boundingrectangle und avarageDepth drin
//            //PXCMFaceData.DetectionData faceDataDetectionData = faceDataFace.QueryDetection();
//            //faceDataDetectionData.QueryFaceAverageDepth();
//            //faceDataDetectionData.QueryBoundingRect();
//
//            senseManager.ReleaseFrame();
//
//            try {
//                Thread.sleep(250);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        if (faceData != null) {
//            faceData.close();
//        }
//
//        finalizeRealSenseSdk();
//    }
//
//    private void trackPose(PXCMFaceData.Face faceDataFace)
//    {
//        PXCMFaceData.PoseData poseData = faceDataFace.QueryPose();
//
//        //TODO: in java anderes object
//        //PXCMFaceData.HeadPosition headPosition;
//        PXCMPoint3DF32 headPosition = new PXCMPoint3DF32();
//        PXCMFaceData.PoseEulerAngles poseAngles = new PXCMFaceData.PoseEulerAngles();
//        //TODO: What the hell is quaternion pose?
//        //PXCMFaceData.PoseQuaternion pose;
//
//        if (poseData.QueryHeadPosition(headPosition)
//                && poseData.QueryPoseAngles(poseAngles)) {
//            isUserTracked = true;
//
//            userPosition = new UserPosition(
//                    new Vector3D(headPosition.x, headPosition.y, headPosition.z),
//                    new Vector3D(poseAngles.roll, poseAngles.pitch, poseAngles.yaw));
//        }
//        else {
//            resetTracking();
//        }
//    }
//
//    private void trackExpressions(PXCMFaceData.Face faceDataFace)
//    {
//        PXCMFaceData.ExpressionsData expressionsData = faceDataFace.QueryExpressions();
//
//        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultKiss = new PXCMFaceData.ExpressionsData.FaceExpressionResult();
//        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultTongueOut = new PXCMFaceData.ExpressionsData.FaceExpressionResult();
//        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultSmile = new PXCMFaceData.ExpressionsData.FaceExpressionResult();
//        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultMouthOpen = new PXCMFaceData.ExpressionsData.FaceExpressionResult();
//
//        if (expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS, faceExpressionResultKiss)
//                && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT, faceExpressionResultTongueOut)
//                && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE, faceExpressionResultSmile)
//                && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN, faceExpressionResultMouthOpen)) {
//            isUserTracked = true;
//
//            userExpressions = new UserExpressions();
//            userExpressions.setKiss(faceExpressionResultKiss.intensity == 100);
//            userExpressions.setTongueOut(faceExpressionResultTongueOut.intensity == 100);
//            userExpressions.setSmile(faceExpressionResultSmile.intensity > 55);
//            userExpressions.setMouthOpen(faceExpressionResultMouthOpen.intensity > 55);
//        }
//        else {
//            resetTracking();
//        }
//    }
//
//    private void resetTracking() {
//        isUserTracked = false;
//
//        userPosition = null;
//        userExpressions = null;
//    }
//}
