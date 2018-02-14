using System;
using System.Threading;

namespace DotNetTracking
{
    public class UserTracker
    {
        #region Members
        private bool IsTracking { get; set; }
        private Thread TrackingThread { get; set; }

        private PXCMCaptureManager CaptureManager { get; set; }
        private PXCMFaceModule FaceModule { get; set; }
        #endregion

        #region Properties
        public bool IsUserTracked { get; set; }

        public float UserHeadPositionX { get; set; }
        public float UserHeadPositionY { get; set; }
        public float UserHeadPositionZ { get; set; }

        public float UserHeadPositionYaw { get; set; }
        public float UserHeadPositionPitch { get; set; }
        public float UserHeadPositionRoll { get; set; }

        public bool UserExpressionKiss { get; set; }
        public bool UserExpressionTongueOut { get; set; }
        public bool UserExpressionSmile { get; set; }
        public bool UserExpressionMouthOpen { get; set; }
        #endregion
        
        public void StartTracking()
        {
            StopTracking();

            SdkCommonHelper.Initialize();

            SetConfiguration();

            pxcmStatus senseManagerInitStatus = SdkCommonHelper.SenseManager.Init();
            if (senseManagerInitStatus < pxcmStatus.PXCM_STATUS_NO_ERROR)
            {
                throw new Exception("SenseManager.Init() error: " + senseManagerInitStatus.ToString());
            }

            StartTrackingThread();
        }

        private void StartTrackingThread()
        {
            IsTracking = true;
            TrackingThread = new Thread(() => 
            {
                PXCMFaceData FaceData = FaceModule.CreateOutput();

                while (IsTracking)
                {
                    pxcmStatus acquireFrameStatus = SdkCommonHelper.SenseManager.AcquireFrame(true);
                    if (acquireFrameStatus < pxcmStatus.PXCM_STATUS_NO_ERROR)
                    {
                        ResetUserTrackData();
                        Console.WriteLine("SenseManager.AcquireFrame(true) error: " + acquireFrameStatus.ToString());
                        Thread.Sleep(250);
                        continue;
                    }

                    FaceData.Update();
                    int numberOfDetectedFaces = FaceData.QueryNumberOfDetectedFaces();
                    if (numberOfDetectedFaces != 1)
                    {
                        ResetUserTrackData();
                        SdkCommonHelper.SenseManager.ReleaseFrame();
                        Thread.Sleep(250);
                        continue;
                    }

                    PXCMFaceData.Face faceDataFace = FaceData.QueryFaceByIndex(0);

                    TrackUserPosition(faceDataFace);
                    TrackUserExpressions(faceDataFace);

                    SdkCommonHelper.SenseManager.ReleaseFrame();

                    Thread.Sleep(100);
                }
            });
            TrackingThread.Start();
        }

        private void TrackUserPosition(PXCMFaceData.Face faceDataFace)
        {
            if (faceDataFace == null)
            {
                ResetUserTrackData();
                return;
            } 

            PXCMFaceData.PoseData poseData = faceDataFace.QueryPose();
            PXCMFaceData.HeadPosition headPosition;
            PXCMFaceData.PoseEulerAngles poseAngles;

            if (poseData != null && 
                poseData.QueryHeadPosition(out headPosition) && poseData.QueryPoseAngles(out poseAngles))
            {
                IsUserTracked = true;

                UserHeadPositionX = headPosition.headCenter.x;
                UserHeadPositionY = headPosition.headCenter.y;
                UserHeadPositionZ = headPosition.headCenter.z;
                
                UserHeadPositionYaw = poseAngles.yaw;
                UserHeadPositionPitch = poseAngles.pitch;
                UserHeadPositionRoll = poseAngles.roll;
            }
            else
            {
                ResetUserTrackData();
            }
        }

        private void TrackUserExpressions(PXCMFaceData.Face faceDataFace)
        {
            if (faceDataFace == null)
            {
                ResetUserTrackData();
                return;
            }

            PXCMFaceData.ExpressionsData expressionsData = faceDataFace.QueryExpressions();
            PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionKiss;
            PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionTongueOut;
            PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionSmile;
            PXCMFaceData.ExpressionsData.FaceExpressionResult faceExpressionMouthOpen;

            if (expressionsData != null &&
                expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS, out faceExpressionKiss)
                && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT, out faceExpressionTongueOut)
                && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE, out faceExpressionSmile)
                && expressionsData.QueryExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN, out faceExpressionMouthOpen))
            {
                IsUserTracked = true;
                UserExpressionKiss = faceExpressionKiss.intensity == 100;
                UserExpressionTongueOut = faceExpressionTongueOut.intensity == 100;
                UserExpressionSmile = faceExpressionSmile.intensity > 55;
                UserExpressionMouthOpen = faceExpressionMouthOpen.intensity > 55;
            }
            else
            {
                ResetUserTrackData();
            }
        }

        public void StopTracking()
        {
            StopTrackingThread();
            SdkCommonHelper.Dispose();
        }

        private void StopTrackingThread()
        {
            if (IsTracking)
            {
                IsTracking = false;
                
                while (TrackingThread.IsAlive)
                {
                    Thread.Sleep(50);
                }
                TrackingThread = null;
            }
        }

        private void SetConfiguration()
        {
            SdkCommonHelper.SenseManager.EnableFace();

            FaceModule = SdkCommonHelper.SenseManager.QueryFace();

            PXCMFaceConfiguration FaceConfiguration = FaceModule.CreateActiveConfiguration();

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
            PXCMFaceConfiguration.ExpressionsConfiguration ExpressionsConfiguration = FaceConfiguration.QueryExpressions();
            ExpressionsConfiguration.properties.isEnabled = true;
            ExpressionsConfiguration.properties.maxTrackedFaces = 1;
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_KISS);
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_TONGUE_OUT);
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_SMILE);
            ExpressionsConfiguration.EnableExpression(PXCMFaceData.ExpressionsData.FaceExpression.EXPRESSION_MOUTH_OPEN);

            //FaceConfiguration.EnableAllAlerts();
            //FaceConfiguration.SubscribeAlert(OnAlert);

            pxcmStatus applyChangesStatus = FaceConfiguration.ApplyChanges();

            if (applyChangesStatus < pxcmStatus.PXCM_STATUS_NO_ERROR)
                throw new Exception("FaceConfiguration.ApplyChanges() error: " + applyChangesStatus.ToString());
        }

        private void ResetUserTrackData()
        {
            IsUserTracked = false;

            UserHeadPositionX = 0;
            UserHeadPositionY = 0;
            UserHeadPositionZ = 0;

            UserHeadPositionYaw = 0;
            UserHeadPositionPitch = 0;
            UserHeadPositionRoll = 0;

            UserExpressionKiss = false;
            UserExpressionTongueOut = false;
            UserExpressionSmile = false;
            UserExpressionMouthOpen = false;
        }

    }
}
