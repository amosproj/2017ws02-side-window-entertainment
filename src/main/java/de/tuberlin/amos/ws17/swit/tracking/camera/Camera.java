//package de.tuberlin.amos.ws17.swit.tracking.camera;
//
//import de.tuberlin.amos.ws17.swit.tracking.camera.*;
//import intel.rssdk.PXCMCapture;
//
//import java.util.List;
//
//public class Camera implements AutoCloseable {
//
//    public String name;
//    public PXCMCapture.Device device;
//    public PXCMCapture.DeviceInfo deviceInfo;
//    //public List<Resolution_ColorDepth_FrameRateCombination> resolution_ColorDepth_FrameRate_Combinations;
//
//    public Camera(
//        String name,
//        PXCMCapture.Device device,
//        PXCMCapture.DeviceInfo deviceInfo)
//        //List<Resolution_ColorDepth_FrameRateCombination> resolution_ColorDepth_FrameRate_Combinations)
//    {
//        this.name = name;
//        this.device = device;
//        this.deviceInfo = deviceInfo;
//        //this.resolution_ColorDepth_FrameRate_Combinations = resolution_ColorDepth_FrameRate_Combinations;
//    }
//
//    @Override
//    public void close() {
//        if (this.device != null) {
//            this.device.close();
//        }
//    }
//}
