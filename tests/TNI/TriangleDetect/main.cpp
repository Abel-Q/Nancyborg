#include <opencv2/opencv.hpp>
//#include "opencv2/objdetect/objdetect.hpp"
//#include "opencv2/highgui/highgui.hpp"
//#include "opencv2/imgproc/imgproc.hpp"

#include <QDebug>
#include <QTcpServer>
#include <QTcpSocket>
#include <QThread>
#include <QApplication>
#include <QLabel>
#include <QMainWindow>

#include <iostream>
#include <stdio.h>

using namespace std;
using namespace cv;



QImage  cvMatToQImage( const cv::Mat &inMat )
   {
      switch ( inMat.type() )
      {
         // 8-bit, 4 channel
         case CV_8UC4:
         {
            QImage image( inMat.data, inMat.cols, inMat.rows, inMat.step, QImage::Format_RGB32 );

            return image;
         }

         // 8-bit, 3 channel
         case CV_8UC3:
         {
            QImage image( inMat.data, inMat.cols, inMat.rows, inMat.step, QImage::Format_RGB888 );

            return image.rgbSwapped();
         }

         // 8-bit, 1 channel
         case CV_8UC1:
         {
            static QVector<QRgb>  sColorTable;

            // only create our color table once
            if ( sColorTable.isEmpty() )
            {
               for ( int i = 0; i < 256; ++i )
                  sColorTable.push_back( qRgb( i, i, i ) );
            }

            QImage image( inMat.data, inMat.cols, inMat.rows, inMat.step, QImage::Format_Indexed8 );

            image.setColorTable( sColorTable );

            return image;
         }

         default:
            qWarning() << "ASM::cvMatToQImage() - cv::Mat image type not handled in switch:" << inMat.type();
            break;
      }

      return QImage();
   }

/** Function Headers */
//void detectAndDisplay(Mat frame);

/** Global variables */
String window_name = "Triangle detection";

/** @function main */
int main(int argc, char** argv)
{
    QApplication a(argc, argv);
    QWidget window;

    QLabel label("Image", &window);
    window.showMaximized();

//	VideoCapture capture;
	VideoCapture capture(0);
	unsigned int i = 0;
//	unsigned int j = 0;
	Mat frame;
	Mat hsv;
	Mat mask_yellow, mask_red;
	vector<vector<Point> > contours_yellow, contours_red;
	vector<Vec4i> hierarchy;
	vector<Point> approx;
	int yellow_lower_h = 15;
	int yellow_lower_s = 60;
	int yellow_lower_v = 100;
	int yellow_upper_h = 35;
	int yellow_upper_s = 255;
	int yellow_upper_v = 255;
	int red_lower_h = 150;
	int red_lower_s = 60;
	int red_lower_v = 100;
	int red_upper_h = 200;
	int red_upper_s = 255;
	int red_upper_v = 255;
	Scalar low_yellow(yellow_lower_h, yellow_lower_s, yellow_lower_v);
	Scalar upp_yellow(yellow_upper_h, yellow_upper_s, yellow_upper_v);
	Scalar low_red(red_lower_h, red_lower_s, red_lower_v);
	Scalar upp_red(red_upper_h, red_upper_s, red_upper_v);

	//-- 2. Read the video stream
	capture.open(-1);

	if(!capture.isOpened())
	{
		printf("--(!)Error opening video capture\n");
		return -1;
	}

	while(capture.read(frame))
	{
		if(frame.empty())
		{
			printf(" --(!) No captured frame -- Break!");
			break;
		}

		cvtColor(frame, hsv, COLOR_BGR2HSV);

		inRange(hsv, low_yellow, upp_yellow, mask_yellow);
		inRange(hsv, low_red, upp_red, mask_red);

		findContours(mask_yellow, contours_yellow, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE);
		findContours(mask_red, contours_red, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE);

		qDebug() << "jaune: " << contours_yellow.size();
		qDebug() << "rouge: " << contours_red.size();

		for(i = 0; i < contours_yellow.size(); i++)
		{
			approxPolyDP(Mat(contours_yellow[i]), approx, arcLength(Mat(contours_yellow[i]), true), true);
			qDebug() << "j: " << arcLength(Mat(contours_yellow[i]), true) << ", " << sizeof(contours_yellow[i]) << ", " << contourArea(Mat(contours_yellow[i]));
//			qDebug() << "jaune: " << approx.rows << ", " << contourArea(contours_yellow[i]);
			drawContours(frame, Mat(contours_yellow[i]), 0, Scalar(0, 255, 255), 2);
			if(sizeof(approx) == 3 && contourArea(Mat(contours_yellow[i])) >= 2)
			{
//				for(j = 0; j < approx.size(); j++)
//				{
//					cout << "jaune: " << contours_yellow[0][j];
//				}
//				for(MatConstIterator_<double> vertex = approx.begin(); vertex != approx.end(); ++vertex)
//				{
//					qDebug() << "jaune: " << *vertex;
//				}
				//contours
			}
		}

		for(i = 0; i < contours_red.size(); i++)
		{
			approxPolyDP(Mat(contours_red[i]), approx, arcLength(Mat(contours_yellow[i]), true), true);
			qDebug() << "r: " << arcLength(Mat(contours_red[i]), true) << ", " << sizeof(contours_red[i]) << ", " << contourArea(Mat(contours_red[i]));
//			qDebug() << "rouge: " << approx.rows << ", " << contourArea(contours_red[i]);
			drawContours(frame, Mat(contours_red[i]), 0, Scalar(0, 0, 255), 2);
			if(sizeof(contours_red[i]) == 3 && contourArea(Mat(contours_red[i])) >= 2)
			{
//				drawContours(frame, Mat(contours_red[i]), i, Scalar(0, 0, 255), 2);
//				for(j = 0; j < approx.size(); j++)
//				{
//					cout << "rouge: " << contours_red[0][j];
//				}
//				for(MatConstIterator_<double> vertex = approx.begin(); vertex != approx.end(); ++vertex)
//				{
//					qDebug() << "rouge: " << *vertex;
//				}
				//contours
			}
		}

		imwrite("/sdcard/test.jpg", frame);
        label.setPixmap(QPixmap::fromImage(cvMatToQImage(frame)));

		QThread::msleep(3000);

		//-- 3. Apply the classifier to the frame
//		detectAndDisplay( frame );

//        int c = waitKey(10);
//        if( (char)c == 27 ) { break; } // escape
	}

    return a.exec();
}
