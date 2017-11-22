package de.tuberlin.amos.ws17.swit.tracking;

import de.tuberlin.amos.ws17.swit.common.Point3D;
import de.tuberlin.amos.ws17.swit.tracking.camera.Camera;
import de.tuberlin.amos.ws17.swit.tracking.camera.CameraService;
import de.tuberlin.amos.ws17.swit.tracking.camera.RealSenseSdkCameraService;
import de.tuberlin.amos.ws17.swit.tracking.camera.Resolution_ColorDepth_FrameRateCombination;

import intel.rssdk.*;

public class RealSenseSdkUserTracker implements UserTracker, Runnable {

    private boolean isUserTracked;
    @Override
    public boolean getIsUserTracked() {
        return isUserTracked;
    }

    private UserHeadPose userHeadPose;
    @Override
    public UserHeadPose getUserHeadPose() {
        return userHeadPose;
    }

    private UserExpressions userExpressions;
    @Override
    public UserExpressions getUserExpressions() {
        return userExpressions;
    }

    private String errorMessage = null;
    private boolean isTracking = false;
    private PXCMSession session;
    private CameraService cameraService;
    private Camera intelRealSenseCamera;
    private Resolution_ColorDepth_FrameRateCombination selectedResolution_ColorDepth_FrameRateCombination;

    private PXCMSenseManager senseManager;
    //private PXCMCaptureManager captureManager;

    private PXCMFaceModule faceModule;
    private PXCMFaceConfiguration faceConfiguration;
    private PXCMFaceConfiguration.ExpressionsConfiguration expressionsConfiguration;

    public RealSenseSdkUserTracker() {
        userHeadPose = new UserHeadPose();
        userExpressions = new UserExpressions();

        session = PXCMSession.CreateInstance();
        cameraService = new RealSenseSdkCameraService(session);
        cameraService.loadCameras();
    }

    @Override
    public boolean startTracking() {
        try {
            selectHardwareAndStreamProfile();
        }
        catch (Exception e) {
            errorMessage = e.getMessage();
            return false;
        }

        try {
            setFaceConfiguration();
        }
        catch (Exception e) {
            errorMessage = e.getMessage();
            return false;
        }
        //init Camera
        pxcmStatus senseManagerInitStatus = senseManager.Init();
        if (senseManagerInitStatus != pxcmStatus.PXCM_STATUS_NO_ERROR)
        {
            errorMessage = "SenseManager.Init() error: " + senseManagerInitStatus.toString();
            return false;
        }

        isTracking = true;

        Thread threadLoop = new Thread(this);
        threadLoop.start();

        return true;
    }

    @Override
    public boolean stopTracking() {
        isTracking = false;

        return false;
    }




    private void selectHardwareAndStreamProfile() throws Exception {
        intelRealSenseCamera = cameraService.GetUserTrackingCamera();
        if (intelRealSenseCamera == null)
            throw new Exception("Intel RealSense Camera not connected!");

        selectedResolution_ColorDepth_FrameRateCombination = cameraService.getResolution_ColorDepth_FrameRateCombination(intelRealSenseCamera);
        if (selectedResolution_ColorDepth_FrameRateCombination == null)
            throw new Exception("Resolution_ColorDepth_FrameRateCombination not supported");

        senseManager = session.CreateSenseManager();
        if (senseManager == null) {
            throw new Exception("session.CreateSenseManager() failed.");
        }
        //captureManager = senseManager..captureManager;
        //captureManager.FilterByDeviceInfo(intelRealSenseCamera.device.deviceInfo);

//        PXCMCapture.Device.StreamProfileSet streamFrofileSet = new PXCMCapture.Device.StreamProfileSet();
//        streamFrofileSet
//        {
//            color =
//                {
//                    frameRate = SelectedResolution_ColorDepth_FrameRateCombination.FrameRate,
//                    imageInfo =
//                        {
//                            format = SelectedResolution_ColorDepth_FrameRateCombination.PixelFormat,
//                            height = SelectedResolution_ColorDepth_FrameRateCombination.Height,
//                            width = SelectedResolution_ColorDepth_FrameRateCombination.Width
//                        }
//                }
//        };
//
//        captureManager.FilterByStreamProfiles(streamFrofileSet);
    }

    private void setFaceConfiguration() throws Exception {
        //TODO: Parameter?
        pxcmStatus enableFaceStatus = senseManager.EnableFace("");
        if (enableFaceStatus != pxcmStatus.PXCM_STATUS_NO_ERROR) {
            throw new Exception("senseManager.EnableFace() failed.");
        }

        faceModule = senseManager.QueryFace();
        if (faceModule == null) {
            throw new Exception("senseManager.QueryFace() failed");
        }

        faceConfiguration = faceModule.CreateActiveConfiguration();
        if (faceConfiguration == null) {
            throw new Exception("faceModule.CreateActiveConfiguration() failed");
        }

        faceConfiguration.SetTrackingMode(PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR_PLUS_DEPTH);
        faceConfiguration.strategy = PXCMFaceConfiguration.TrackingStrategyType.STRATEGY_CLOSEST_TO_FARTHEST;

        //Detection
        //FaceConfiguration.detection.isEnabled = false;
        //FaceConfiguration.detection.maxTrackedFaces = 0;

        //Landmarks
        faceConfiguration.landmarks.isEnabled = true;
        faceConfiguration.landmarks.maxTrackedFaces = 1;
        faceConfiguration.landmarks.smoothingLevel = PXCMFaceConfiguration.SmoothingLevelType.SMOOTHING_DISABLED;

        //Configuration of Pose
        faceConfiguration.pose.isEnabled = true;
        faceConfiguration.pose.maxTrackedFaces = 1;
        faceConfiguration.pose.smoothingLevel = PXCMFaceConfiguration.SmoothingLevelType.SMOOTHING_DISABLED;

        //Configuration of Gaze
        //FaceConfiguration.

        //Configuration of Expressions
        expressionsConfiguration = faceConfiguration.QueryExpressions();
        if (expressionsConfiguration == null) {
            throw new Exception("faceConfiguration.QueryExpressions() failed");
        }

        expressionsConfiguration.properties.isEnabled = true;
        expressionsConfiguration.properties.maxTrackedFaces = 1;
        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS);
        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT);
        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE);
        expressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN);
        //TODO ist das überhaupt nötig?
        //faceConfiguration.EnableAllAlerts();
        //faceConfiguration.SubscribeAlert(OnAlert);

        pxcmStatus applyChangesStatus = faceConfiguration.ApplyChanges();

        if (applyChangesStatus != pxcmStatus.PXCM_STATUS_NO_ERROR)
            throw new Exception("FaceConfiguration.ApplyChanges() error: " + applyChangesStatus.toString());
    }

    //Thread looping
    @Override
    public void run() {

        PXCMFaceData faceData = faceModule.CreateOutput();

        while (isTracking) {

            pxcmStatus acquireFrameStatus = senseManager.AcquireFrame(true);
            if (acquireFrameStatus != pxcmStatus.PXCM_STATUS_NO_ERROR)
            {
                resetTracking();
                System.out.println("SenseManager.AcquireFrame(true) error: " + acquireFrameStatus.toString());
                continue;
            }
            //TODO: Image Daten holen aber erstmal CaptureManager verstehen auf java Seite
//            PXCMCapture.Sample captureSample = senseManager.QueryFaceSample();
//            if (captureSample == null)
//            {
//                resetTracking();
//                senseManager.ReleaseFrame();
//                continue;
//            }
            //
            //TrackImageData(captureSample);

            faceData.Update();

            int numberOfDetectedFaces = faceData.QueryNumberOfDetectedFaces();
            if (numberOfDetectedFaces != 1)
            {
                resetTracking();
                senseManager.ReleaseFrame();
                continue;
            }

            PXCMFaceData.Face faceDataFace = faceData.QueryFaceByIndex(0);

            trackPose(faceDataFace);
            trackExpressions(faceDataFace);
            //TrackLandmarks(faceDataFace);
            //TrackGaze();


            //FaceData.QueryRecognitionModule();

            //im nächsten object steckt boundingrectangle und avarageDepth drin
            //PXCMFaceData.DetectionData faceDataDetectionData = faceDataFace.QueryDetection();
            //faceDataDetectionData.QueryFaceAverageDepth();
            //faceDataDetectionData.QueryBoundingRect();

            senseManager.ReleaseFrame();

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        if (faceData != null) {
            faceData.close();
        }

        faceConfiguration.close();
        senseManager.close();
    }

    private void trackPose(PXCMFaceData.Face faceDataFace)
    {
        PXCMFaceData.PoseData poseData = faceDataFace.QueryPose();

        //TODO: in java anderes object
        //PXCMFaceData.HeadPosition headPosition;
        PXCMPoint3DF32 headPosition = new PXCMPoint3DF32();
        PXCMFaceData.PoseEulerAngles poseAngles = new PXCMFaceData.PoseEulerAngles();
        //TODO: What the hell is quaternion pose?
        //PXCMFaceData.PoseQuaternion pose;

        if (poseData.QueryHeadPosition(headPosition) && poseData.QueryPoseAngles(poseAngles))
        {
            userHeadPose.isTracked = true;
            userHeadPose.centerOfHead = new Point3D(headPosition.x, headPosition.y, headPosition.z);
//            {
//                X = headPosition.headCenter.x,
//                Y = headPosition.headCenter.y,
//                Z = headPosition.headCenter.z
//            };
            userHeadPose.yaw = poseAngles.yaw;
            userHeadPose.pitch = poseAngles.pitch;
            userHeadPose.roll = poseAngles.roll;
        }
        else
        {
            userHeadPose.isTracked = false;
        }
    }

    private void trackExpressions(PXCMFaceData.Face faceDataFace)
    {
        PXCMFaceData.ExpressionsData expressionsData = faceDataFace.QueryExpressions();

        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultKiss = new PXCMFaceData.ExpressionsData.FaceExpressionResult();
        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultTongueOut = new PXCMFaceData.ExpressionsData.FaceExpressionResult();
        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultSmile = new PXCMFaceData.ExpressionsData.FaceExpressionResult();
        PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResultMouthOpen = new PXCMFaceData.ExpressionsData.FaceExpressionResult();

        if (expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS, faceExpressionResultKiss)
            && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT, faceExpressionResultTongueOut)
            && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE, faceExpressionResultSmile)
            && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN, faceExpressionResultMouthOpen))
        {
            userExpressions.isTracked = true;
            userExpressions.isKiss = faceExpressionResultKiss.intensity == 100;
            userExpressions.isTongueOut = faceExpressionResultTongueOut.intensity == 100;
            userExpressions.isSmile = faceExpressionResultSmile.intensity > 55;
            userExpressions.isMouthOpen = faceExpressionResultMouthOpen.intensity > 55;
        }
        else
        {
            userExpressions.isTracked = false;
        }
    }

    private void resetTracking() {

    }
}
