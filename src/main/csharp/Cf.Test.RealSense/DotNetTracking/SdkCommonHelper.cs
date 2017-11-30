using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace DotNetTracking
{
    public static class SdkCommonHelper
    {
        public static PXCMSession Session { get; private set; }
        public static PXCMSenseManager SenseManager { get; private set; }
                
        public static Camera IntelRealSense300RS { get; set; }

        public static void Initialize()
        {
            Session = PXCMSession.CreateInstance();
            SenseManager = Session.CreateSenseManager();
        }

        public static void Dispose()
        {
            if (SenseManager != null)
            {
                SenseManager.Close();
                SenseManager.Dispose();
                SenseManager = null;
            }
            
            if (Session != null)
            {
                Session.Dispose();
                Session = null;
            }
        }
    }
}
