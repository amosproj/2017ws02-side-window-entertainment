using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Cf.Test.RealSense
{
    public class UserExpressions
    {
        public bool Tracked { get; set; }

        public bool Kiss { get; set; }
        public bool TongueOut { get; set; }
        public bool Smile { get; set; }
        public bool MouthOpen { get; set; }

        public void Reset()
        {
            Tracked = false;

            Kiss = false;
            TongueOut = false;
            Smile = false;
            MouthOpen = false;
        }

        public override string ToString()
        {
            return Tracked
                ? "Expressions tracked (Kiss:" + Kiss.ToString() + ", TongueOut:" + TongueOut.ToString() + ", Smile:" + Smile.ToString() + ", MouthOpen:" + MouthOpen.ToString() + ")"
                : "Expressions not tracked";
        }
    }
}
