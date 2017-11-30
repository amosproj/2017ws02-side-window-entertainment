using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Cf.Test.RealSense
{
    public class Point
    {
        public float X { get; set; }
        public float Y { get; set; }
        public float Z { get; set; }

        public override string ToString()
        {
            return "Point (" 
                + X.ToString("0.00") + "," 
                + Y.ToString("0.00") + "," 
                + Z.ToString("0.00") + ")";
        }
    }
}
