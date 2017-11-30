using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Cf.Test.RealSense
{
    public class UserHeadPose
    {
        public bool Tracked { get; set; }

        public Point Center { get; set; }

        public float Yaw { get; set; }
        public float Pitch { get; set; }
        public float Roll { get; set; }

        public void Reset()
        {
            Tracked = false;
            Center = null;
            Yaw = 0;
            Pitch = 0;
            Roll = 0;
        }

        public override string ToString()
        {
            return Tracked
                ? "Pose tracked (Center:" + Center.ToString() + ") (Yaw:" + Yaw.ToString() + ", Pitch:" + Pitch.ToString() + ", Roll:" + Roll.ToString() + ")"
                : "Pose not tracked";
        }
    }
}
