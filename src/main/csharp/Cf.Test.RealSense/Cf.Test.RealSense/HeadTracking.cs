using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace Cf.Test.RealSense
{
    public class HeadTracking
    {
        #region Interface
        public UserHeadPose UserHeadPose = null;
        public UserExpressions UserExpressions = null;
        public WriteableBitmap writeableBitmap = null;
        //public UserLandmarks UserLandmarks { get; set; }
        #endregion

        #region Fields
        private volatile bool _shouldStop = false;
        #endregion

        #region RealSense SDK Classes
        private PXCMSession Session { get; set; }
        
        private PXCMSenseManager SenseManager { get; set; }
        private PXCMCaptureManager CaptureManager { get; set; }

        private PXCMFaceModule FaceModule { get; set; }
        private PXCMFaceConfiguration FaceConfiguration { get; set; }
        private PXCMFaceConfiguration.ExpressionsConfiguration ExpressionsConfiguration { get; set; }

        //private PXCMFaceData FaceData { get; set; }
        #endregion

        #region Hardware Configuration
        public CameraService CameraService { get; set; }
        public Camera SelectedIntelRealSenseCamera { get; set; }
        public Resolution_ColorDepth_FrameRateCombination SelectedResolution_ColorDepth_FrameRateCombination { get; set; }
        #endregion
        
        #region Constructor
        public HeadTracking()
        {
            UserHeadPose = new UserHeadPose();
            UserExpressions = new UserExpressions();
            //UserLandmarks = new UserLandmarks();

            Session = PXCMSession.CreateInstance();
            CameraService = new CameraService(Session);
        }
        #endregion
        
        public void StartTracking()
        {
            SelectHardwareAndStreamProfile();

            SetFaceConfiguration();

            pxcmStatus senseManagerInitStatus = SenseManager.Init();
            if (senseManagerInitStatus < pxcmStatus.PXCM_STATUS_NO_ERROR)
            {
                throw new Exception("SenseManager.Init() error: " + senseManagerInitStatus.ToString());
            }

            _shouldStop = false;

            Thread threadLoop = new Thread(this.StartTrackingLoop);
            threadLoop.Start();
        }

        private void SelectHardwareAndStreamProfile()
        {
            SelectedIntelRealSenseCamera = CameraService.GetIntelRealSenseSR300();
            if (SelectedIntelRealSenseCamera == null)
                throw new Exception("Intel RealSense Camera not connected!");
            
            SelectedResolution_ColorDepth_FrameRateCombination = CameraService.GetResolution_ColorDepth_FrameRateCombination(SelectedIntelRealSenseCamera);
            if (SelectedResolution_ColorDepth_FrameRateCombination == null)
                throw new Exception("Resolution_ColorDepth_FrameRateCombination not supported");

            SenseManager = Session.CreateSenseManager();
            CaptureManager = SenseManager.captureManager;
            CaptureManager.FilterByDeviceInfo(SelectedIntelRealSenseCamera.Device.deviceInfo);

            var streamFrofileSet = new PXCMCapture.Device.StreamProfileSet
            {
                color =
                    {
                        frameRate = SelectedResolution_ColorDepth_FrameRateCombination.FrameRate,
                        imageInfo =
                        {
                            format = SelectedResolution_ColorDepth_FrameRateCombination.PixelFormat,
                            height = SelectedResolution_ColorDepth_FrameRateCombination.Height,
                            width = SelectedResolution_ColorDepth_FrameRateCombination.Width
                        }
                    }
            };

            CaptureManager.FilterByStreamProfiles(streamFrofileSet);
        }
        
        private void SetFaceConfiguration()
        {
            SenseManager.EnableFace();

            FaceModule = SenseManager.QueryFace();

            FaceConfiguration = FaceModule.CreateActiveConfiguration();
            
            FaceConfiguration.SetTrackingMode(PXCMFaceConfiguration.TrackingModeType.FACE_MODE_COLOR_PLUS_DEPTH);
            FaceConfiguration.strategy = PXCMFaceConfiguration.TrackingStrategyType.STRATEGY_CLOSEST_TO_FARTHEST;

            //Detection
            //FaceConfiguration.detection.isEnabled = false;
            //FaceConfiguration.detection.maxTrackedFaces = 0;
            
            //Landmarks
            FaceConfiguration.landmarks.isEnabled = true;
            FaceConfiguration.landmarks.maxTrackedFaces = 1;
            FaceConfiguration.landmarks.smoothingLevel = PXCMFaceConfiguration.SmoothingLevelType.SMOOTHING_DISABLED;
            
            //Configuration of Pose
            FaceConfiguration.pose.isEnabled = true;
            FaceConfiguration.pose.maxTrackedFaces = 1;
            FaceConfiguration.pose.smoothingLevel = PXCMFaceConfiguration.SmoothingLevelType.SMOOTHING_DISABLED;

            //Configuration of Gaze
            //FaceConfiguration.

            //Configuration of Expressions
            ExpressionsConfiguration = FaceConfiguration.QueryExpressions();
            ExpressionsConfiguration.properties.isEnabled = true;
            ExpressionsConfiguration.properties.maxTrackedFaces = 1;
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS);
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT);
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE);
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN);
            
            FaceConfiguration.EnableAllAlerts();
            FaceConfiguration.SubscribeAlert(OnAlert);

            pxcmStatus applyChangesStatus = FaceConfiguration.ApplyChanges();

            if (applyChangesStatus < pxcmStatus.PXCM_STATUS_NO_ERROR)
                throw new Exception("FaceConfiguration.ApplyChanges() error: " + applyChangesStatus.ToString());
        }

        private void StartTrackingLoop()
        {
            PXCMFaceData FaceData = FaceModule.CreateOutput();

            while (!_shouldStop)
            {
                pxcmStatus acquireFrameStatus = SenseManager.AcquireFrame(true);
                if (acquireFrameStatus < pxcmStatus.PXCM_STATUS_NO_ERROR)
                {
                    ResetTrackData();
                    Console.WriteLine("SenseManager.AcquireFrame(true) error: " + acquireFrameStatus.ToString());
                    continue;
                }
                
                PXCMCapture.Sample captureSample = SenseManager.QueryFaceSample();
                if (captureSample == null)
                {
                    ResetTrackData();
                    SenseManager.ReleaseFrame();
                    continue;
                }
                //TODO: Image Daten holen
                TrackImageData(captureSample);

                FaceData.Update();

                int numberOfDetectedFaces = FaceData.QueryNumberOfDetectedFaces();
                if (numberOfDetectedFaces != 1)
                {
                    ResetTrackData();
                    SenseManager.ReleaseFrame();
                    continue;
                }

                PXCMFaceData.Face faceDataFace = FaceData.QueryFaceByIndex(0);

                TrackPose(faceDataFace);
                TrackExpressions(faceDataFace);
                //TrackLandmarks(faceDataFace);
                //TrackGaze();
                

                //FaceData.QueryRecognitionModule();

                //im nächsten object steckt boundingrectangle und avarageDepth drin
                //PXCMFaceData.DetectionData faceDataDetectionData = faceDataFace.QueryDetection();
                //faceDataDetectionData.QueryFaceAverageDepth();
                //faceDataDetectionData.QueryBoundingRect();

                SenseManager.ReleaseFrame();

                Thread.Sleep(250);
            }

            if (FaceData != null)
                FaceData.Dispose();

            FaceConfiguration.Dispose();
            SenseManager.Close();
            SenseManager.Dispose();
        }

        private void TrackPose(PXCMFaceData.Face faceDataFace)
        {
            PXCMFaceData.PoseData poseData = faceDataFace.QueryPose();

            PXCMFaceData.HeadPosition headPosition;
            PXCMFaceData.PoseEulerAngles poseAngles;
            //TODO: What the hell is quaternion pose?
            //PXCMFaceData.PoseQuaternion pose;
            
            if (poseData.QueryHeadPosition(out headPosition) && poseData.QueryPoseAngles(out poseAngles))
            {
                UserHeadPose.Tracked = true;
                UserHeadPose.Center = new Point()
                {
                    X = headPosition.headCenter.x,
                    Y = headPosition.headCenter.y,
                    Z = headPosition.headCenter.z
                };
                UserHeadPose.Yaw = poseAngles.yaw;
                UserHeadPose.Pitch = poseAngles.pitch;
                UserHeadPose.Roll = poseAngles.roll;
            }
            else
            {
                UserHeadPose.Reset();
            }
        }
        
        private void TrackExpressions(PXCMFaceData.Face faceDataFace)
        {
            PXCMFaceData.ExpressionsData expressionsData = faceDataFace.QueryExpressions();

            PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionResult;
            if (expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS, out faceExpressionResult))
            {
                UserExpressions.Tracked = true;
                UserExpressions.Kiss = faceExpressionResult.intensity == 100;
            }
            else
            {
                UserExpressions.Reset();
            }

            if (expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT, out faceExpressionResult))
            {
                UserExpressions.Tracked = true;
                UserExpressions.TongueOut = faceExpressionResult.intensity == 100;
            }
            else
            {
                UserExpressions.Reset();
            }
            
            if (expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE, out faceExpressionResult))
            {
                UserExpressions.Tracked = true;
                UserExpressions.Smile = faceExpressionResult.intensity > 55;
                Console.WriteLine("UserExpressions.Smile.intensity:" + faceExpressionResult.intensity.ToString());
            }
            else
            {
                UserExpressions.Reset();
            }

            if (expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN, out faceExpressionResult))
            {
                UserExpressions.Tracked = true;
                UserExpressions.MouthOpen = faceExpressionResult.intensity > 55;
                Console.WriteLine("UserExpressions.MouthOpen.intensity:" + faceExpressionResult.intensity.ToString());
            }
            else
            {
                UserExpressions.Reset();
            }
        }
        
        //private void TrackLandmarks(PXCMFaceData.Face faceDataFace)
        //{
        //    PXCMFaceData.LandmarksData landmarksData = faceDataFace.QueryLandmarks();

        //    PXCMFaceData.LandmarkPoint[] landmarkPoints;
        //    if (landmarksData.QueryPointsByGroup(PXCMFaceData.LandmarksGroupType.LANDMARK_GROUP_NOSE, out landmarkPoints))
        //    {
        //        foreach (var landmarkPoint in landmarkPoints)
        //        {
        //            Console.WriteLine("landmarkPoint: " + landmarkPoint.world.ToString());
        //        }
        //    }
        //}

        //private void TrackGaze(PXCMFaceData.Face faceDataFace)
        //{
            
        //    faceDataFace.
        //    faceDataFace.QueryGaze().QueryGazePoint().confidence
        //}

        private void TrackImageData(PXCMCapture.Sample captureSample)
        {
            PXCMImage imageColor = captureSample.color;
            PXCMImage.ImageData imageData;
            pxcmStatus aquireAccessStatus = imageColor.AcquireAccess(PXCMImage.Access.ACCESS_READ, PXCMImage.PixelFormat.PIXEL_FORMAT_RGB32, out imageData);
            if (aquireAccessStatus >= pxcmStatus.PXCM_STATUS_NO_ERROR)
            {
                writeableBitmap = imageData.ToWritableBitmap(0, imageColor.info.width, imageColor.info.height, 96, 96);
                writeableBitmap.Freeze();
            }

            imageColor.ReleaseAccess(imageData);
        }
        
        public void StopTracking()
        {
            _shouldStop = true;
        }
        
        private void ResetTrackData()
        {
            UserHeadPose.Reset();
            UserExpressions.Reset();
        }
        
        private void OnAlert(PXCMFaceData.AlertData alertData)
        {
            Debug.WriteLine(alertData.label.ToString());
        }

    }
}
