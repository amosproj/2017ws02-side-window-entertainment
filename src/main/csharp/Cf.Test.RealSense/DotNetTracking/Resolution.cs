using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DotNetTracking
{
    public class Resolution
    {
        public int Width { get; set; }
        public int Height { get; set; }

        public Resolution(int width, int height)
        {
            Width = width;
            Height = height;
        }

        public override bool Equals(object obj)
        {
            var resolution = obj as Resolution;
            return resolution != null &&
                   Width == resolution.Width &&
                   Height == resolution.Height;
        }

        public override int GetHashCode()
        {
            var hashCode = 859600377;
            hashCode = hashCode * -1521134295 + Width.GetHashCode();
            hashCode = hashCode * -1521134295 + Height.GetHashCode();
            return hashCode;
        }
    }
}
