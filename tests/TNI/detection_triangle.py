#!/usr/bin/python
# -*- coding: utf-8 -*-
#

import cv2
import numpy as np

cam = cv2.VideoCapture(0)
#src = cv2.imread('test_tri.png')
#pattern = cv2.imread('TriangleJaune.png')
#gray = cv2.cvtColor(pattern, cv2.COLOR_BGR2GRAY)
#gray = np.float32(gray)
#dst = cv2.cornerHarris(gray, 2, 3, 0.04)
#dst = cv2.dilate(dst, None)
#pattern[dst>0.01*dst.max()] = [0, 0, 255]
#cv2.imshow("dst", pattern)

color = "red"
threshold1 = 150
threshold2 = 255
num_contour = -1
yellow = (0, 255, 255)
red = (0, 0, 255)
yellow_lower_h = 15
yellow_lower_s = 60
yellow_lower_v = 100
yellow_upper_h = 35
yellow_upper_s = 255
yellow_upper_v = 255
red_lower_h = 150
red_lower_s = 60
red_lower_v = 100
red_upper_h = 200
red_upper_s = 255
red_upper_v = 255

def checkin(val, min, max):
	if val < min:
		val = min
	elif val > max:
		val = max
	return val

while True:
	ret, img = cam.read()
#	img = src
#	canny = cv2.Canny(img, threshold1, threshold2)

#	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
#	cv2.imshow("gray", gray)
	hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
#	cv2.imshow("canny", canny)
	low_yellow = np.array([yellow_lower_h, yellow_lower_s, yellow_lower_v], dtype=np.uint8)
	upp_yellow = np.array([yellow_upper_h, yellow_upper_s, yellow_upper_v], dtype=np.uint8)
	low_red = np.array([red_lower_h, red_lower_s, red_lower_v], dtype=np.uint8)
	upp_red = np.array([red_upper_h, red_upper_s, red_upper_v], dtype=np.uint8)

	mask_yellow = cv2.inRange(hsv, low_yellow, upp_yellow)
	mask_red = cv2.inRange(hsv, low_red, upp_red)

#	res_yellow = cv2.bitwise_and(img, img, mask=mask_yellow)
#	res_red = cv2.bitwise_and(img, img, mask=mask_red)

	contours_yellow, hierarchy = cv2.findContours(mask_yellow, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
	contours_red, hierarchy = cv2.findContours(mask_red, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
#	contours, hierarchy = cv2.findContours(canny, 1, 2)
#	tri_yellow = ""	
#	tri_red = ""
	for cnt in contours_yellow:
		approx = cv2.approxPolyDP(cnt, 0.1*cv2.arcLength(cnt, True), True)
		if len(approx) == 3 and cv2.contourArea(cnt) >= 2000:
			cv2.drawContours(img, [cnt], 0, yellow, 2)
			for vertex in approx:
				cv2.circle(img, (vertex[0][0], vertex[0][1]), 5, (255, 0, 0), -1)
#			tri_yellow = approx				

	for cnt in contours_red:
		approx = cv2.approxPolyDP(cnt, 0.1*cv2.arcLength(cnt, True), True)
		if len(approx) == 3 and cv2.contourArea(cnt) >= 2000:
			cv2.drawContours(img, [cnt], 0, red, 2)
			for vertex in approx:
				cv2.circle(img, (vertex[0][0], vertex[0][1]), 5, (0, 255, 0), -1)
#			tri_red = approx
	
	# cv2.circle: color in BGR, not RGB !
	# for vertex in tri_yellow:
	# 	cv2.circle(img, (vertex[0][0], vertex[0][1]), 5, (255, 0, 0), -1)

	# for vertex in tri_red:
	# 	cv2.circle(img, (vertex[0][0], vertex[0][1]), 5, (0, 255, 0), -1)

	cv2.imshow("img", img)
#	cv2.imshow("mask", mask)
#	cv2.imshow("res_yellow", res_yellow)
#	cv2.imshow("res_red", res_red)
#	cv2.imshow("cont", cont)

#	img = cv2.Canny(img, threshold1, threshold2)
#	cv2.imshow("canny", img)
#	contours, hierarchy = cv2.findContours(img, 1, 2)
#	cnt = contours[0]

#	(x, y), radius = cv2.minEnclosingCircle(cnt)
#	center = (int(x), int(y))
#	radius = int(radius)
#	img = cv2.circle(img, center, radius, (0, 255, 0), 2)
#	x, y, w, h = cv2.boundingRect(cnt)
#	img = cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)

#	dst = cv2.cornerHarris(img, 2, 3, 0.04)
#	dst = cv2.dilate(dst, None)
#	pattern[dst>0.01*dst.max()] = [0, 0, 255]
#	cv2.imshow("dst", pattern)

#	cv2.imshow("image", img)

	key = cv2.waitKey(10) & 0xFF
	if key == ord('q'):
		break
	# elif key == ord('1'):
	# 	threshold1 -= 1
	# elif key == ord('2'):
	# 	threshold1 += 1
	# elif key == ord('3'):
	# 	threshold2 -= 1
	# elif key == ord('4'):
	# 	threshold2 += 1
	# elif key == ord('a'):
	# 	if color == "yellow":
	# 		yellow_lower_h -= 1
	# 	elif color == "red":
	# 		red_lower_h -= 1
	# elif key == ord('s'):
	# 	if color == "yellow":
	# 		yellow_lower_h += 1
	# 	elif color == "red":
	# 		red_lower_h += 1
	# elif key == ord('d'):
	# 	if color == "yellow":
	# 		yellow_lower_s -= 1
	# 	elif color == "red":
	# 		red_lower_s -= 1
	# elif key == ord('f'):
	# 	if color == "yellow":
	# 		yellow_lower_s += 1
	# 	elif color == "red":
	# 		red_lower_s += 1
	# elif key == ord('g'):
	# 	if color == "yellow":
	# 		yellow_lower_v -= 1
	# 	elif color == "red":
	# 		red_lower_v -= 1
	# elif key == ord('h'):
	# 	if color == "yellow":
	# 		yellow_lower_v += 1
	# 	elif color == "red":
	# 		red_lower_v += 1
	# elif key == ord('z'):
	# 	if color == "yellow":
	# 		yellow_upper_h -= 1
	# 	elif color == "red":
	# 		red_upper_h -= 1
	# elif key == ord('x'):
	# 	if color == "yellow":
	# 		yellow_upper_h += 1
	# 	elif color == "red":
	# 		red_upper_h += 1
	# elif key == ord('c'):
	# 	if color == "yellow":
	# 		yellow_upper_s -= 1
	# 	elif color == "red":
	# 		red_upper_s -= 1
	# elif key == ord('v'):
	# 	if color == "yellow":
	# 		yellow_upper_s += 1
	# 	elif color == "red":
	# 		red_upper_s += 1
	# elif key == ord('b'):
	# 	if color == "yellow":
	# 		yellow_upper_v -= 1
	# 	elif color == "red":
	# 		red_upper_v -= 1
	# elif key == ord('n'):
	# 	if color == "yellow":
	# 		yellow_upper_v += 1
	# 	elif color == "red":
	# 		red_upper_v += 1
	# elif key == ord('-'):
	# 	num_contour -= 1
	# elif key == ord('+'):
	# 	num_contour += 1
	# elif key == ord('r'):
	# 	color = "red"
	# elif key == ord('y'):
	# 	color = "yellow"
	
	# num_contour = checkin(num_contour, -1, 255)
	# yellow_lower_h = checkin(yellow_lower_h, 0, 255)
	# yellow_lower_s = checkin(yellow_lower_s, 0, 255)
	# yellow_lower_v = checkin(yellow_lower_v, 0, 255)
	# yellow_upper_h = checkin(yellow_upper_h, 0, 255)
	# yellow_upper_s = checkin(yellow_upper_s, 0, 255)
	# yellow_upper_v = checkin(yellow_upper_v, 0, 255)
	# red_lower_h = checkin(red_lower_h, 0, 255)
	# red_lower_s = checkin(red_lower_s, 0, 255)
	# red_lower_v = checkin(red_lower_v, 0, 255)
	# red_upper_h = checkin(red_upper_h, 0, 255)
	# red_upper_s = checkin(red_upper_s, 0, 255)
	# red_upper_v = checkin(red_upper_v, 0, 255)
	# threshold1 = checkin(threshold1, 0, 255)
	# threshold2 = checkin(threshold2, 0, 255)

#	if color == "yellow":
#		print("[" + str(yellow_lower_h) + ", " + str(yellow_lower_s) + ", " + str(yellow_lower_v) + "]; [" + str(yellow_upper_h) + ", " + str(yellow_upper_s) + ", " + str(yellow_upper_v) + "]")
#	elif color == "red":
#		print("[" + str(red_lower_h) + ", " + str(red_lower_s) + ", " + str(red_lower_v) + "]; [" + str(red_upper_h) + ", " + str(red_upper_s) + ", " + str(red_upper_v) + "]")

#	if threshold1 > 255:
#		threshold1 = 255
#	elif threshold1 < 0:
#		threshold1 = 0

#	if threshold2 > 255:
#		threshold2 = 255
#	elif threshold2 < 0:
#		threshold2 = 0
	
#	print(str(threshold1) + ", " + str(threshold2))

