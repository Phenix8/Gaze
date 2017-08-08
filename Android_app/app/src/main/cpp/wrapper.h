#ifndef ANDROID_APP_WRAPPER_H
#define ANDROID_APP_WRAPPER_H

#include <jni.h>


#define loadDetectors Java_com_bof_gaze_detection_ObjectDetector_loadDetectors
#define checkForObjects Java_com_bof_gaze_detection_ObjectDetector_checkForObjects
#define getMessage Java_com_bof_gaze_detection_ObjectDetector_getMessage

extern "C" {

jint    loadDetectors(JNIEnv *, jobject, jobject, jstring);
jint    checkForObjects(JNIEnv *, jobject, jobject, jint, jint, jstring, jint zoomLevel);
jstring getMessage(JNIEnv *env, jobject obj);

};

#endif //ANDROID_APP_WRAPPER_H
