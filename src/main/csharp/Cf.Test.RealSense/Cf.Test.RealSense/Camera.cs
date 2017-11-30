using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Cf.Test.RealSense
{
    public class Camera
    {
        public string Name { get; }
        public PXCMCapture.Device Device { get; }
        public List<Resolution_ColorDepth_FrameRateCombination> Resolution_ColorDepth_FrameRate_Combinations { get; }

        public Camera(string name, PXCMCapture.Device device, List<Resolution_ColorDepth_FrameRateCombination> resolution_ColorDepth_FrameRate_Combinations)
        {
            Name = name;
            Device = device;
            Resolution_ColorDepth_FrameRate_Combinations = resolution_ColorDepth_FrameRate_Combinations;
        }
    }
}
