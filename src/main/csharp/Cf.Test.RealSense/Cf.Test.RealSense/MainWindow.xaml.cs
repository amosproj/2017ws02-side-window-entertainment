using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Cf.Test.RealSense
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //HeadTracking HeadTracking = null;
        DotNetTracking.UserTracker UserTracker = null;

        public MainWindow()
        {
            InitializeComponent();

            this.Closing += MainWindow_Closing;
        }

        private void MainWindow_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            //if (HeadTracking != null)
            //{
            //    HeadTracking.StopTracking();
            //    HeadTracking = null;
            //}
        }

        private void AddTextBlock(string text)
        {
            StackPanel.Children.Insert(0,new TextBlock() { Text = text });

            if (StackPanel.Children.Count > 20)
                StackPanel.Children.RemoveAt(19);
        }

        private void ButtonStartTracking_Click(object sender, RoutedEventArgs e)
        {
            UserTracker = new DotNetTracking.UserTracker();
            UserTracker.StartTracking();
            //HeadTracking = new HeadTracking();
            //HeadTracking.StartTracking();
            AddTextBlock("Tracking started");

            ButtonStartTracking.IsEnabled = false;
        }

        private void ButtonGetPose_Click(object sender, RoutedEventArgs e)
        {
            AddTextBlock(UserTracker.IsUserTracked.ToString());
        }

        private void ButtonGetExpressions_Click(object sender, RoutedEventArgs e)
        {
            //AddTextBlock(HeadTracking.UserExpressions.ToString());
        }

        private void ButtonStopTracking_Click(object sender, RoutedEventArgs e)
        {
            UserTracker.StopTracking();
            //HeadTracking.StopTracking();
            //HeadTracking = null;
            AddTextBlock("Tracking stoped");
        }

        private void ButtonGetImage_Click(object sender, RoutedEventArgs e)
        {
            //Image.Source = HeadTracking.writeableBitmap;
        }
    }
}
