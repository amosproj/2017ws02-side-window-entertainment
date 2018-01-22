using System;
using System.Collections.Generic;
using System.IO;
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
            //AddTextBlock(UserTracker.ImageString);
            //Image.Source = UserTracker.WriteableBitmap;// ByteImageConverter.ByteToImage(UserTracker.ImageAsByteArray);// LoadImage(UserTracker.ImageAsByteArray);
        }

        public class ByteImageConverter
        {
            public static ImageSource ByteToImage(byte[] imageData)
            {
                BitmapImage biImg = new BitmapImage();
                MemoryStream ms = new MemoryStream(imageData);
                biImg.BeginInit();
                biImg.StreamSource = ms;
                biImg.EndInit();

                ImageSource imgSrc = biImg as ImageSource;

                return imgSrc;
            }
        }

        private static BitmapSource LoadImage(byte[] imageData)
        {
            //WriteableBitmap writeableBitmap = new WriteableBitmap(1270, 720, 96, 96, Pixelfo)
            return BitmapSource.Create(
                1280, 
                720, 
                96, 
                96, 
                PixelFormats.Bgr32, 
                new BitmapPalette(new List<Color>() { Colors.Red, Colors.Green, Colors.Blue, Colors.Transparent }), 
                imageData, 1280/32);

            if (imageData == null || imageData.Length == 0) return null;
            var image = new BitmapImage();
            using (var mem = new MemoryStream(imageData))
            {
                mem.Position = 0;
                image.BeginInit();
                image.CreateOptions = BitmapCreateOptions.PreservePixelFormat;
                image.CacheOption = BitmapCacheOption.OnLoad;
                image.UriSource = null;
                image.StreamSource = mem;
                image.EndInit();
            }
            image.Freeze();
            return image;
        }
    }
}
