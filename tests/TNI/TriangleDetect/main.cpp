#include <opencv2/opencv.hpp>
//#include "opencv2/objdetect/objdetect.hpp"
//#include "opencv2/highgui/highgui.hpp"
//#include "opencv2/imgproc/imgproc.hpp"

#include <QDebug>
#include <QThread>
#include <QFile>

#include <iostream>
#include <fstream>
#include <string>
#include <stdio.h>

using namespace std;
using namespace cv;

/** Global variables */
String window_name = "Triangle detection";

/** @function main */
int main()
{
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
	QFile tni_conf("/sdcard/tni.conf");
	QTextStream text_tni_conf(&tni_conf);
	int yellow_lower_h = 15;
	int yellow_lower_s = 60;
	int yellow_lower_v = 100;
	int yellow_upper_h = 35;
	int yellow_upper_s = 255;
	int yellow_upper_v = 255;
	int red_lower_h = 160; //130
	int red_lower_s = 60; //50
	int red_lower_v = 100;
	int red_upper_h = 200;
	int red_upper_s = 255;
	int red_upper_v = 255;
	Scalar low_yellow;
	Scalar upp_yellow;
	Scalar low_red;
	Scalar upp_red;

	//-- 2. Read the video stream
	capture.open(-1);

	capture.set(CV_CAP_PROP_FRAME_WIDTH, 800);
	capture.set(CV_CAP_PROP_FRAME_HEIGHT, 480);

	if(!capture.isOpened())
	{
		printf("--(!)Error opening video capture\n");
		return -1;
	}

	tni_conf.open(QIODevice::ReadOnly | QIODevice::Text);

	while(capture.read(frame))
	{
		if(frame.empty())
		{
			printf(" --(!) No captured frame -- Break!");
			break;
		}

		tni_conf.seek(0);
		yellow_lower_h = text_tni_conf.readLine().toInt();
		yellow_lower_s = text_tni_conf.readLine().toInt();
		yellow_lower_v = text_tni_conf.readLine().toInt();
		yellow_upper_h = text_tni_conf.readLine().toInt();
		yellow_upper_s = text_tni_conf.readLine().toInt();
		yellow_upper_v = text_tni_conf.readLine().toInt();
		red_lower_h = text_tni_conf.readLine().toInt();
		red_lower_s = text_tni_conf.readLine().toInt();
		red_lower_v = text_tni_conf.readLine().toInt();
		red_upper_h = text_tni_conf.readLine().toInt();
		red_upper_s = text_tni_conf.readLine().toInt();
		red_upper_v = text_tni_conf.readLine().toInt();

		low_yellow = Scalar(yellow_lower_h, yellow_lower_s, yellow_lower_v);
		upp_yellow = Scalar(yellow_upper_h, yellow_upper_s, yellow_upper_v);
		low_red = Scalar(red_lower_h, red_lower_s, red_lower_v);
		upp_red = Scalar(red_upper_h, red_upper_s, red_upper_v);

		cvtColor(frame, hsv, COLOR_BGR2HSV);

		inRange(hsv, low_yellow, upp_yellow, mask_yellow);
		inRange(hsv, low_red, upp_red, mask_red);

		findContours(mask_yellow, contours_yellow, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE);
		findContours(mask_red, contours_red, hierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE);

		qDebug() << "jaune: " << contours_yellow.size();
		qDebug() << "rouge: " << contours_red.size();

		for(i = 0; i < contours_yellow.size(); i++)
		{
			approxPolyDP(Mat(contours_yellow[i]), approx, 0.1*arcLength(Mat(contours_yellow[i]), true), true);
//			qDebug() << "j: " << arcLength(Mat(contours_yellow[i]), true) << ", " << sizeof(contours_yellow[i]) << ", " << contourArea(Mat(contours_yellow[i]));
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
			approxPolyDP(Mat(contours_red[i]), approx, 0.1*arcLength(Mat(contours_yellow[i]), true), true);
//			qDebug() << "r: " << arcLength(Mat(contours_red[i]), true) << ", " << sizeof(contours_red[i]) << ", " << contourArea(Mat(contours_red[i]));
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

		QThread::msleep(3000);

		//-- 3. Apply the classifier to the frame
//		detectAndDisplay( frame );

//        int c = waitKey(10);
//        if( (char)c == 27 ) { break; } // escape
	}

	tni_conf.close();

	return 0;
}
