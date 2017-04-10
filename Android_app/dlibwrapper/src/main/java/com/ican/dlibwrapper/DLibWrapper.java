package com.ican.dlibwrapper;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.util.Random;

public class DLibWrapper {

    private static DLibWrapper instance = null;

    public static DLibWrapper getInstance() {
        if (instance == null) {
            instance = new DLibWrapper();
        }

        return instance;
    }

    private DLibWrapper() {
        System.loadLibrary("dlib");
        System.loadLibrary("dlib-wrapper");
    }

    private native int checkForObjects(ByteBuffer y, int width, int height);

    public native int loadDetectors(AssetManager mgr, String detectorsDirectory);
    public native String getMessage();

    public int checkForObjects(Image image, Activity activity) {
        TestActivity act = (TestActivity) activity;

        return this.checkForObjects(
                image.getPlanes()[0].getBuffer(),
                image.getWidth(),
                image.getHeight()
        );
    }

    public static String getProcessorABI() {
        return Build.SUPPORTED_ABIS[0];
    }

    public static String[] getSupportedABIs() {
        return Build.SUPPORTED_ABIS;
    }
}
