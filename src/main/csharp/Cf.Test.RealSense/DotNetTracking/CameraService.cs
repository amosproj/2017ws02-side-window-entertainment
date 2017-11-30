using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DotNetTracking
{
    public class CameraService
    {
        #region Fields
        private string intelRealSenseSR300Name = "Intel(R) RealSense(TM) 3D Camera SR300";
        //TODO: name of logitech z920 oder so und variablen namen anpassen
        private string logitechCamera = "Integrated Camera";

        private readonly List<Resolution> supportedColorResolutions = new List<Resolution>
        {
            new Resolution(1920, 1080),
            new Resolution(1280, 720),
            new Resolution(960, 540),
            new Resolution(640, 480),
            new Resolution(640, 360),
        };

        public readonly Dictionary<string, PXCMFaceConfiguration.TrackingModeType> faceTrackingModes =
           new Dictionary<string, PXCMFaceConfiguration.TrackingModeType>()
           {
                { "3D Tracking", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR_PLUS_DEPTH },
                { "2D Tracking", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR },
                { "IR Tracking", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_IR },
                { "2D Still", PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR_STILL }
           };
        #endregion

        #region Properties
        //private PXCMSession Session { get; set; }

        public Dictionary<string, Camera> Cameras { get; set; }
        #endregion

        #region Constructor
        public CameraService()//)
        {
            //Session = session;

            
        }
        #endregion

        #region Interface
        public bool SelectIntelRealSenseSR300()
        {
            foreach (var camera in Cameras.Values)
            {
                if (camera.Name.Equals(intelRealSenseSR300Name))
                {
                    SdkCommonHelper.IntelRealSense300RS = camera;
                    return true;
                }
            }
            return false;
        }

        //public Resolution_ColorDepth_FrameRateCombination GetResolution_ColorDepth_FrameRateCombination(Camera camera)
        //{
        //    return camera.Resolution_ColorDepth_FrameRate_Combinations.Last();
        //}

        public bool SelectLogitech()
        {
            //foreach (var camera in Cameras.Values)
            //{
            //    if (camera.Name == logitechCamera)
            //        return camera;
            //}
            return false;
        }

        //public System.Drawing.Bitmap GetBitmap()
        //{
            //TODO: julian, vorher muss aber noch die Camera gestartet werden, dann erst sollte die funktion funktionieren
            //TODO: Project FaceTracking DisplayPicture und dann ganz viel Code :-)
        //    return null;
        //}
        #endregion

        #region Functions
        public void LoadCameras()
        {
            Cameras = new Dictionary<string, Camera>();
            PXCMSession session = SdkCommonHelper.Session;

            var groupDescribtion = new PXCMSession.ImplDesc
            {
                group = PXCMSession.ImplGroup.IMPL_GROUP_SENSOR,
                subgroup = PXCMSession.ImplSubgroup.IMPL_SUBGROUP_VIDEO_CAPTURE
            };

            for (int i = 0; ; i++)
            {
                
                PXCMSession.ImplDesc desc1;
                
                if (session.QueryImpl(groupDescribtion, i, out desc1) < pxcmStatus.PXCM_STATUS_NO_ERROR)
                {
                    break;
                }

                PXCMCapture capture;
                if (session.CreateImpl(desc1, out capture) < pxcmStatus.PXCM_STATUS_NO_ERROR)
                {
                    continue;
                }

                for (int j = 0; ; j++)
                {
                    PXCMCapture.DeviceInfo deviceInfo;
                    if (capture.QueryDeviceInfo(j, out deviceInfo) < pxcmStatus.PXCM_STATUS_NO_ERROR)
                    {
                        break;
                    }

                    PXCMCapture.Device device = capture.CreateDevice(j);
                    if (device == null)
                    {
                        throw new Exception("PXCMCapture.Device null");
                    }

                    string name = deviceInfo.name;
                    if (Cameras.ContainsKey(name))
                    {
                        name += j;
                    }

                    Cameras.Add(name, new Camera(
                        name, 
                        device, 
                        GetResolution_ColorDepth_FrameRate_Combinations(device).OrderBy(x => x.Width).ToList()));
                }

                capture.Dispose();
            }
        }

        private List<Resolution_ColorDepth_FrameRateCombination> GetResolution_ColorDepth_FrameRate_Combinations(PXCMCapture.Device device)
        {
            var deviceResolutions = new List<Resolution_ColorDepth_FrameRateCombination>();

            for (int k = 0; k < device.QueryStreamProfileSetNum(PXCMCapture.StreamType.STREAM_TYPE_COLOR); k++)
            {
                PXCMCapture.Device.StreamProfileSet profileSet;
                device.QueryStreamProfileSet(PXCMCapture.StreamType.STREAM_TYPE_COLOR, k, out profileSet);

                var currentRes = new Resolution_ColorDepth_FrameRateCombination(profileSet.color.imageInfo, profileSet.color.frameRate);

                if (IsProfileSupported(profileSet, device.deviceInfo))
                    continue;
                //Filter only supported Resolutions
                if (supportedColorResolutions.Contains(new Resolution(currentRes.Width, currentRes.Height)))
                    deviceResolutions.Add(currentRes);
            }

            return deviceResolutions;
        }

        private static bool IsProfileSupported(PXCMCapture.Device.StreamProfileSet profileSet, PXCMCapture.DeviceInfo dinfo)
        {
            return
                (profileSet.color.frameRate.min < 30) ||
                (dinfo != null && dinfo.model == PXCMCapture.DeviceModel.DEVICE_MODEL_DS4 &&
                (profileSet.color.imageInfo.width == 1920 || profileSet.color.frameRate.min > 30 || profileSet.color.imageInfo.format == PXCMImage.PixelFormat.PIXEL_FORMAT_YUY2)) ||
                (profileSet.color.options == PXCMCapture.Device.StreamOption.STREAM_OPTION_UNRECTIFIED);
        }

        #endregion
    }
}
