package de.tuberlin.amos.ws17.swit.tracking.camera;

import de.tuberlin.amos.ws17.swit.tracking.camera.*;
import intel.rssdk.PXCMCapture;

import java.util.List;

public class Camera {

    public String name;
    public PXCMCapture.Device device;
    public List<Resolution_ColorDepth_FrameRateCombination> resolution_ColorDepth_FrameRate_Combinations;

    public Camera(
        String name,
        PXCMCapture.Device device,
        List<Resolution_ColorDepth_FrameRateCombination> resolution_ColorDepth_FrameRate_Combinations)
    {
        this.name = name;
        this.device = device;
        this.resolution_ColorDepth_FrameRate_Combinations = resolution_ColorDepth_FrameRate_Combinations;
    }

}
