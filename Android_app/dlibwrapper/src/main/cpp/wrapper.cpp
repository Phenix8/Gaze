#include "wrapper.h"

#include <EGL/egl.h>
#include <GLES/gl.h>

#include <string>
#include <stdexcept>
#include <iostream>
#include <istream>
#include <streambuf>
#include <vector>
#include <cstring>

#include <android/asset_manager_jni.h>
#include <android/log.h>

#include <dlib/svm_threaded.h>
#include <dlib/image_processing.h>
#include <dlib/data_io.h>

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

inline jobject array2dToBitmap(JNIEnv *env, dlib::array2d<unsigned char> &image) {
    jclass bitmapConfig = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID rgba8888FieldID = env->GetStaticFieldID(bitmapConfig, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    jobject rgba8888Obj = env->GetStaticObjectField(bitmapConfig, rgba8888FieldID);

    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethodID = env->GetStaticMethodID(bitmapClass,"createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject bitmapObj = env->CallStaticObjectMethod(bitmapClass, createBitmapMethodID, image.nc(), image.nr(), rgba8888Obj);

    jintArray pixels = env->NewIntArray(image.nc() * image.nr());
    int i = 0;
    do
    {
        unsigned char red = image.element();
        unsigned char green = image.element();
        unsigned char blue = image.element();
        unsigned char alpha = image.element();
        int currentPixel = (alpha << 24) | (red << 16) | (green << 8) | (blue);
        env->SetIntArrayRegion(pixels, i, 1, &currentPixel);
        i++;
    } while (image.move_next());

    jmethodID setPixelsMid = env->GetMethodID(bitmapClass, "setPixels", "([IIIIIII)V");
    env->CallVoidMethod(bitmapObj, setPixelsMid, pixels, 0, image.nc(), 0, 0, image.nc(), image.nr());
}

inline dlib::array2d<unsigned char> rgb2Array2d(
        JNIEnv *env,
        jobject rBuffer, jobject gBuffer, jobject bBuffer,
        jint w, jint h, jint ps, jint rs) {

    LOGI("test2");
    unsigned char *rData = (unsigned char *) env->GetDirectBufferAddress(rBuffer);
    unsigned char *gData = (unsigned char *) env->GetDirectBufferAddress(gBuffer);
    unsigned char *bData = (unsigned char *) env->GetDirectBufferAddress(bBuffer);
    LOGI("test3");
    dlib::array2d<unsigned char> result((int)h, (int)w);

    size_t index;

    for (size_t i=0; i<h; i++) {
        for (size_t j=0; j<w; j++) {
            index = (i * rs) * (w * ps) + (j * ps);
            result[i][j] = R_COEF * rData[index] + G_COEF * gData[index] + B_COEF * bData[index];
        }
    }
    LOGI("test4");

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

jobject checkForObjects(JNIEnv *env, jobject obj,
                     jobject rBuffer, jobject gBuffer, jobject bBuffer,
                     jint width, jint height,
                     jint pixelStride, jint rowStride) {

    LOGI("test");

    dlib::array2d<unsigned char> image =
            rgb2Array2d(env, rBuffer, gBuffer, bBuffer, width, height, pixelStride, rowStride);

    std::vector<dlib::rectangle> dets = detectors[0](image);

    return array2dToBitmap(env, image);

    //return (jint) dets.size() > 0 ? 1 : 0;
}

jstring getMessage(JNIEnv *env, jobject obj) {
    return env->NewStringUTF(message);
}