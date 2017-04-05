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
        System.loadLibrary("c++_shared");
        System.loadLibrary("dlib");
        System.loadLibrary("dlib-wrapper");
    }

    private native Bitmap checkForObjects(ByteBuffer r, ByteBuffer g, ByteBuffer b, int width, int height, int pixelStride, int rowStride);

    public native int loadDetectors(AssetManager mgr, String detectorsDirectory);
    public native String getMessage();

    public int checkForObjects(Image image, Activity activity) {
        Image.Plane[] planes = image.getPlanes();
        Image.Plane plane0 = planes[0];

        Bitmap bitmap = this.checkForObjects(
            plane0.getBuffer(),
            image.getPlanes()[1].getBuffer(),
            image.getPlanes()[2].getBuffer(),
            image.getWidth(),
            image.getHeight(),
            plane0.getPixelStride(),
            plane0.getRowStride()
        );

        File myDir = new File(activity.getExternalFilesDir(null), "pic.jpg");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getProcessorABI() {
        return Build.SUPPORTED_ABIS[0];
    }

    public static String[] getSupportedABIs() {
        return Build.SUPPORTED_ABIS;
    }
}
