package de.tuberlin.amos.ws17.swit.tracking.camera;

import intel.rssdk.*;
import org.bytedeco.javacpp.opencv_dnn;

import java.util.*;

public class RealSenseSdkCameraService implements CameraService {

    private String intelRealSenseSR300Name = "Intel(R) RealSense(TM) 3D Camera SR300";
    private String logitechCamera = "Integrated Camera";

    private static List<Resolution> supportedColorResolutions;
    private static void initializeSupportedColorResolutions() {
        supportedColorResolutions = new ArrayList<Resolution>();
        supportedColorResolutions.add(new Resolution(1920, 1080));
        supportedColorResolutions.add(new Resolution(1280, 720));
        supportedColorResolutions.add(new Resolution(960, 540));
        supportedColorResolutions.add(new Resolution(640, 480));
        supportedColorResolutions.add(new Resolution(640, 360));
    }

    private static Map<String, PXCMFaceConfiguration.TrackingModeType> trackingModeTypes;
    private static void initializeTrackingModeTypes() {
        trackingModeTypes = new HashMap<String, PXCMFaceConfiguration.TrackingModeType>();
        trackingModeTypes.put("3D Tracking", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR_PLUS_DEPTH);
        trackingModeTypes.put("2D Tracking", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR);
        //anscheinend nicht im java sdk enthalten
        //trackingModeTypes.put("IR Tracking", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_IR);
        trackingModeTypes.put("2D Still", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR_STILL);
    }

    private PXCMSession session;
    private Map<String, Camera> cameras;

    //static initializer
    static
    {
        initializeSupportedColorResolutions();
        initializeTrackingModeTypes();
    }

    public RealSenseSdkCameraService(PXCMSession session) {
        this.session = session;
    }

    @Override
    public void loadCameras() {
        cameras = new HashMap<String, Camera>();

        PXCMSession.ImplDesc groupDescribtion = new PXCMSession.ImplDesc();
        groupDescribtion.group = EnumSet.of(PXCMSession.ImplGroup.IMPL_GROUP_SENSOR);
        groupDescribtion.subgroup = EnumSet.of(PXCMSession.ImplSubgroup.IMPL_SUBGROUP_VIDEO_CAPTURE);

        for (int i = 0; ; i++) {
            //TODO: muss initialisiert werden
            PXCMSession.ImplDesc desc1 = new PXCMSession.ImplDesc();
            if (session.QueryImpl(groupDescribtion, i, desc1) != pxcmStatus.PXCM_STATUS_NO_ERROR) {
                break;
            }
            //TODO: muss initialisiert werden
            PXCMCapture capture = new PXCMCapture();
            if (session.CreateImpl(desc1, capture) != pxcmStatus.PXCM_STATUS_NO_ERROR) {
                continue;
            }

            for (int j = 0; ; j++)
            {
                //TODO: muss initialisiert werden
                PXCMCapture.DeviceInfo deviceInfo = new PXCMCapture.DeviceInfo();
                if (capture.QueryDeviceInfo(j, deviceInfo) != pxcmStatus.PXCM_STATUS_NO_ERROR) {
                    break;
                }

                PXCMCapture.Device device = capture.CreateDevice(j);
                if (device == null) {
                    continue;
                }

                String deviceInfoName = deviceInfo.name;
                if (cameras.containsKey(deviceInfoName)) {
                    deviceInfoName += j;
                }

                cameras.put(deviceInfoName, new Camera(
                    deviceInfoName,
                    device,
                    getResolution_ColorDepth_FrameRate_Combinations(device)));
            }

            capture.close();
        }

    }

    public List<Resolution_ColorDepth_FrameRateCombination> getResolution_ColorDepth_FrameRate_Combinations(PXCMCapture.Device device) {
        List<Resolution_ColorDepth_FrameRateCombination> result = new ArrayList<Resolution_ColorDepth_FrameRateCombination>();

        for (int k = 0; k < device.QueryStreamProfileSetNum(PXCMCapture.StreamType.STREAM_TYPE_COLOR); k++)
        {
            //TODO: muss initialisiert werden
            PXCMCapture.Device.StreamProfileSet profileSet = new PXCMCapture.Device.StreamProfileSet();
            device.QueryStreamProfileSet(PXCMCapture.StreamType.STREAM_TYPE_COLOR, k, profileSet);

            Resolution_ColorDepth_FrameRateCombination currentRes = new Resolution_ColorDepth_FrameRateCombination(profileSet.color.imageInfo, profileSet.color.frameRate);
            //TODO: was steckt eigentlich noch alles in device drin? (viele Queries...)
            //TODO: filter wieder einbauen
            //if (isProfileSupported(profileSet, device.deviceInfo))
            //    continue;
            //Filter only supported Resolutions
            if (supportedColorResolutions.contains(new Resolution(currentRes.imageInfo.width, currentRes.imageInfo.height)))
                result.add(currentRes);
        }

        return result;
    }

//    private static boolean isProfileSupported(PXCMCapture.Device.StreamProfileSet profileSet, PXCMCapture.DeviceInfo dinfo)
//    {
//        return
//            (profileSet.color.frameRate.min < 30) ||
//                (dinfo != null && dinfo.model == PXCMCapture.DeviceModel.DEVICE_MODEL_DS4 &&
//                    (profileSet.color.imageInfo.width == 1920 || profileSet.color.frameRate.min > 30 || profileSet.color.imageInfo.format == PXCMImage.PixelFormat.PIXEL_FORMAT_YUY2)) ||
//                (profileSet.color.options == PXCMCapture.Device.StreamOption.STREAM_OPTION_UNRECTIFIED);
//    }

    @Override
    public Camera GetUserTrackingCamera() {
        for (Camera camera:cameras.values()) {
            if (camera.name == intelRealSenseSR300Name)
                return camera;
        }
        return null;
    }

    @Override
    public Camera GetLandscapeTrackingCamera() {
        return null;
    }

    @Override
    public Resolution_ColorDepth_FrameRateCombination getResolution_ColorDepth_FrameRateCombination(Camera camera) {
        return null;
        //camera.resolution_ColorDepth_FrameRate_Combinations
    }
}
