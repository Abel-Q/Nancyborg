TEMPLATE = app
CONFIG += console

QT += core multimedia

#QMAKE_LIBS += `pkg-config opencv --libs`
#QMAKE_LIBDIR += /home/loic/OpenCV-2.4.9-android-sdk/sdk/native/libs/armeabi-v7a/

LIBS += $$PWD/android/libs/armeabi-v7a/*
#LIBS += /home/loic/OpenCV-2.4.9-android-sdk/sdk/native/libs/armeabi-v7a/*

INCLUDEPATH += /home/loic/OpenCV-2.4.9-android-sdk/sdk/native/jni/include/

SOURCES += \
    main.cpp

CONFIG += mobility
MOBILITY =

ANDROID_PACKAGE_SOURCE_DIR = $$PWD/android

OTHER_FILES += \
	android/AndroidManifest.xml
