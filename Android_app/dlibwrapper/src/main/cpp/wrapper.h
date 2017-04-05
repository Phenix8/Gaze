//
// Created by T0179626 on 03/04/2017.
//

#ifndef ANDROID_APP_WRAPPER_H
#define ANDROID_APP_WRAPPER_H

#include <jni.h>

#define loadDetectors Java_com_ican_dlibwrapper_DLibWrapper_loadDetectors
#define checkForObjects Java_com_ican_dlibwrapper_DLibWrapper_checkForObjects
#define getMessage Java_com_ican_dlibwrapper_DLibWrapper_getMessage

extern "C" {

jint    loadDetectors(JNIEnv *, jobject, jobject, jstring);
jobject checkForObjects(JNIEnv *, jobject, jobject, jobject, jobject, jint, jint, jint, jint);
jstring getMessage(JNIEnv *env, jobject obj);

};

#endif //ANDROID_APP_WRAPPER_H
