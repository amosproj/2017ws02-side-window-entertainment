using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DotNetTracking
{
    public class Resolution_ColorDepth_FrameRateCombination
    {
        public PXCMImage.ImageInfo ImageInfo { get; private set; }
        public PXCMRangeF32 FrameRate { get; private set; }

        public PXCMImage.PixelFormat PixelFormat
        {
            get
            {
                return ImageInfo.format;
            }
        }

        public int Width
        {
            get
            {
                return ImageInfo.width;
            }
        }

        public int Height
        {
            get
            {
                return ImageInfo.height;
            }
        }

        //public float FrameRate
        //{
        //    get
        //    {
        //        return RangeF32.min;
        //    }
        //}
        
        public Resolution_ColorDepth_FrameRateCombination(PXCMImage.ImageInfo imageInfo, PXCMRangeF32 frameRate)
        {
            ImageInfo = imageInfo;
            FrameRate = frameRate;
        }
    }
}
