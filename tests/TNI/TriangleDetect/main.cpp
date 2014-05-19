#include <opencv2/opencv.hpp>
//#include "opencv2/objdetect/objdetect.hpp"
//#include "opencv2/highgui/highgui.hpp"
//#include "opencv2/imgproc/imgproc.hpp"

#include <iostream>
#include <stdio.h>

using namespace std;
using namespace cv;

/** Function Headers */
void detectAndDisplay(Mat frame);

/** Global variables */
String window_name = "Triangle detection";

/** @function main */
int main( void )
{
//	VideoCapture capture;
	VideoCapture capture(0);
	Mat frame;

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

		//-- 3. Apply the classifier to the frame
		detectAndDisplay( frame );

//        int c = waitKey(10);
//        if( (char)c == 27 ) { break; } // escape
	}

    return 0;
}

/** @function detectAndDisplay */
void detectAndDisplay(Mat frame)
{
	Mat frame_gray;

	cvtColor(frame, frame_gray, COLOR_BGR2GRAY);
	equalizeHist(frame_gray, frame_gray);
	//-- Show what you got
//	imshow( window_name, frame );
}
