package com.bof.gaze.detection;

import android.content.res.AssetManager;
import android.media.Image;

import java.nio.ByteBuffer;

public class ObjectDetector {

    private static ObjectDetector instance = null;

    public static ObjectDetector getInstance() {
        if (instance == null) {
            instance = new ObjectDetector();
        }

        return instance;
    }

    private ObjectDetector() {
        //System.loadLibrary("dlib");
        System.loadLibrary("dlib-wrapper");
    }

    private native int checkForObjects(ByteBuffer y, int width, int height, String detectorName, int zoomLevel);
    public native int loadDetectors(AssetManager mgr, String detectorsDirectory);
    public native String getMessage();
    public int checkForObjects(Image image, String detectorName, int zoomLevel) {
       return this.checkForObjects(
                image.getPlanes()[0].getBuffer(),
                image.getWidth(),
                image.getHeight(),
                detectorName,
                zoomLevel
        );
    }
}
