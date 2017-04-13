package dlibwrapper;

import android.content.res.AssetManager;
import android.media.Image;
import android.os.Build;

import java.nio.ByteBuffer;

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

    private native int checkForObjects(ByteBuffer y, int width, int height, String detectorName);
    public native int loadDetectors(AssetManager mgr, String detectorsDirectory);
    public native String getMessage();
    public int checkForObjects(Image image, String detectorName) {
       return this.checkForObjects(
                image.getPlanes()[0].getBuffer(),
                image.getWidth(),
                image.getHeight(),
                detectorName
        );
    }

    public static String getProcessorABI() {
        return Build.SUPPORTED_ABIS[0];
    }

    public static String[] getSupportedABIs() {
        return Build.SUPPORTED_ABIS;
    }
}
