#include "wrapper.h"

#include <string>
#include <stdexcept>
#include <iostream>
#include <istream>
#include <streambuf>
#include <vector>
#include <cstring>

#include <android/native_activity.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>

#include <dlib/svm_threaded.h>
#include <dlib/image_processing.h>
#include <dlib/data_io.h>
#include <dlib/image_io.h>

#define DEBUG 1

#define R_COEF 0.333f
#define G_COEF 0.333f
#define B_COEF 0.333f


#define  LOG_TAG    "DlibW"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

struct membuf : std::streambuf
{
    membuf(char* begin, char* end) {
        this->setg(begin, begin, end);
    }
};

typedef dlib::scan_fhog_pyramid<dlib::pyramid_down<6> > image_scanner_type;
typedef dlib::object_detector<image_scanner_type> detector;

static std::vector<detector> detectors;
static char message[1000];

void setMessage(const char *newMessage) {
    strncpy(message, newMessage, 1000);
}

inline dlib::array2d<unsigned char> byteBufferToArray2d(
        JNIEnv *env, jobject yBuffer, jint w, jint h) {

    //Get a pointer to the Java ByteBuffer's data.
    //We can do that because ByteBuffer returned by
    //android.media.Image.getPlanes()[x].getBuffer()
    //are guaranted to store data contiguously.
    jbyte *yData = (jbyte *) env->GetDirectBufferAddress(yBuffer);

    //We need to rotate image of -PI/2 because dlib and android
    //don't store image the same way.
    //So result will be an array of w rows and h columns
    dlib::array2d<unsigned char> result((int)h, (int)w);
    //And to finish we fill the result column by column
    //starting by the last.
    /*for (int i=h-1; i>-1; i--) {
        for (int j=0; j<w; j++) {
            result[j][i] = *(yData++);
        }
    }*/

    memcpy(&(result[0][0]), yData, w * h);

    return result;
}

jint loadDetectors(JNIEnv *env, jobject obj, jobject assetManager, jstring detectorsDirectory) {

    detector d;
    char completeFileName[100];
    const char *currentFileName = NULL;

    if (detectors.size() > 0) {
        return 0;
    }

    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);

    const char *directory = env->GetStringUTFChars(detectorsDirectory, NULL);
    AAssetDir *dir = AAssetManager_openDir(mgr, directory);

#ifdef DEBUG
    if (dir == NULL) {
        return -1;
    }
#endif

    while ( (currentFileName = AAssetDir_getNextFileName(dir) ) != NULL) {

        strncpy(completeFileName, directory, 99);
        strncat(completeFileName, "/", 99 - strlen(completeFileName));
        strncat(completeFileName, currentFileName, 99 - strlen(completeFileName));

        LOGI("Loading %s\n", completeFileName);

        AAsset *detectorAsset = AAssetManager_open(
                mgr,
                completeFileName,
                AASSET_MODE_BUFFER
        );

#ifdef DEBUG
        if (detectorAsset == NULL) {
            AAssetDir_close(dir);
            env->ReleaseStringUTFChars(detectorsDirectory, directory);
            return -2;
        }
#endif

        char *buffer = (char *) AAsset_getBuffer(detectorAsset);

#ifdef DEBUG
        if (buffer == NULL) {
            AAssetDir_close(dir);
            env->ReleaseStringUTFChars(detectorsDirectory, directory);
            return -3;
        }
#endif

        membuf sbuf(buffer, buffer + AAsset_getLength(detectorAsset));
        std::istream in(&sbuf);

#ifdef DEBUG
        try {
#endif
            dlib::deserialize(d, in);
#ifdef DEBUG
        } catch (const std::exception &e) {
            AAssetDir_close(dir);
            env->ReleaseStringUTFChars(detectorsDirectory, directory);
            return -5;
        }
#endif
        detectors.push_back(d);

        AAsset_close(detectorAsset);
    }

    AAssetDir_close(dir);
    env->ReleaseStringUTFChars(detectorsDirectory, directory);

    return detectors.size();
}

jint checkForObjects(JNIEnv *env, jobject obj,
                     jobject yBuffer, jint width, jint height) {

    dlib::array2d<unsigned char> image =
            byteBufferToArray2d(env, yBuffer, width, height);

    const std::vector<dlib::rectangle> dets = detectors[0](image, 0);

    //dlib::save_jpeg(image, "/storage/emulated/0/Android/data/com.ican.anamorphoses_jsdn/files/test.jpg");

    ///storage/emulated/0/Android/data/com.ican.anamorphoses_jsdn.debug/files/

    return (dets.size() > 0 ? (jint) 1 : (jint) 0);
}

jstring getMessage(JNIEnv *env, jobject obj) {
    return env->NewStringUTF(message);
}