//
// Created by T0179626 on 03/04/2017.
//

#ifndef ANDROID_APP_WRAPPER_H
#define ANDROID_APP_WRAPPER_H

#include <jni.h>

#define loadDetectors Java_dlibwrapper_DLibWrapper_loadDetectors
#define checkForObjects Java_dlibwrapper_DLibWrapper_checkForObjects
#define getMessage Java_dlibwrapper_DLibWrapper_getMessage

extern "C" {

jint    loadDetectors(JNIEnv *, jobject, jobject, jstring);
jint    checkForObjects(JNIEnv *, jobject, jobject, jint, jint);
jstring getMessage(JNIEnv *env, jobject obj);

};

#endif //ANDROID_APP_WRAPPER_H
