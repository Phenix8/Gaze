package com.ican.gaze.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.ican.gaze.view.AutoFitTextureView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by root on 27/07/2017.
 */

public class CameraProcessor implements TextureView.SurfaceTextureListener, ImageReader.OnImageAvailableListener {

    private static final String TAG = "CameraProcessor";

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public interface CameraProcessorListener {
        void onError(String error);
        void onImageAvailable(Image img);
    }

    private CameraProcessorListener listener;

    private Context context;
    private AutoFitTextureView textureView;
    private ImageReader imageReader;

    private HandlerThread handlerThread;
    private Handler handler;

    private CameraDevice camera;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder requestBuilder;

    private Semaphore cameraOpenCloseLock = new Semaphore(1);

    private Size optimalSize = null;
    private Rect sensorArraySize;
    private boolean isMeteringAreaAFSupported;

    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            try {
                camera = cameraDevice;
                createCaptureSession(cameraDevice);
            } catch (CameraAccessException e) {
                listener.onError("Error configuring camera");
                e.printStackTrace();
            } finally {
                unlockCamera();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            listener.onError("Error accessing camera.");
        }
    };

    @Override
    public void onImageAvailable(ImageReader imageReader) {
        Image img = imageReader.acquireNextImage();
        listener.onImageAvailable(img);
        img.close();
    }

    private void startBackgroundHandler() {
        handlerThread = new HandlerThread("cameraHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private void stopBackgroundHandler() {
        boolean joined = false;
        handlerThread.quitSafely();
        while (!joined) {
            try{
                handlerThread.join();
                joined = true;
            } catch (InterruptedException e) {}
        }
        handlerThread = null;
        handler = null;
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e("CameraActivity", "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == optimalSize) {
            return;
        }

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, optimalSize.getHeight(), optimalSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
        matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
        float scale = Math.max(
                (float) viewHeight / optimalSize.getHeight(),
                (float) viewWidth / optimalSize.getWidth());

        matrix.postScale(scale, scale, centerX, centerY);
        matrix.postRotate(270, centerX, centerY);

        textureView.setTransform(matrix);
    }

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

    };

    private void createCaptureSession(CameraDevice cameraDevice) throws CameraAccessException {

        requestBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        Surface surface = new Surface(textureView.getSurfaceTexture());
        requestBuilder.addTarget(surface);

        requestBuilder.setTag("PreviewSession");

        cameraDevice.createCaptureSession(Arrays.asList(new Surface[]{surface, imageReader.getSurface()}),
                new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                captureSession = cameraCaptureSession;

                try {
                    requestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                    captureSession.setRepeatingRequest(requestBuilder.build(),
                            captureCallback, handler);
                } catch (CameraAccessException e) {
                    listener.onError("An error occured while configuring preview.");
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                listener.onError("An error occured while configuring preview.");
            }
        }, handler);
    }

    private String chooseCamera(CameraManager cameraManager)
            throws CameraAccessException {

        CameraCharacteristics cameraInfo;
        StreamConfigurationMap configurationMap;
        Collection<Size> sizes;

        for (String cameraId : cameraManager.getCameraIdList()) {
            cameraInfo = cameraManager.getCameraCharacteristics(cameraId);

            //Check camera is back facing.
            Integer facing = cameraInfo.get(CameraCharacteristics.LENS_FACING);
            if (facing == null) {
                continue;
            }

            if (facing != CameraCharacteristics.LENS_FACING_BACK) {
                continue;
            }

            configurationMap = cameraInfo.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (configurationMap == null) {
                continue;
            }

            Size[] availablesSize = configurationMap.getOutputSizes(ImageFormat.YUV_420_888);
            if (availablesSize == null) {
                continue;
            }

            sizes = Arrays.asList(availablesSize);

            if (sizes.contains(new Size(640, 480))) {
                optimalSize = chooseOptimalSize(
                    availablesSize,
                    textureView.getWidth(),
                    textureView.getHeight(),
                    1920, 1080,
                    Collections.max(sizes, new CompareSizesByArea())
                );

                Log.d(TAG, String.format("preview size : %dx%d", optimalSize.getWidth(), optimalSize.getHeight()));
                Log.d(TAG, String.format("texture size size : %dx%d", textureView.getWidth(), textureView.getHeight()));

                textureView.getSurfaceTexture().setDefaultBufferSize(optimalSize.getWidth(), optimalSize.getHeight());
                textureView.setAspectRatio(optimalSize.getWidth(), optimalSize.getHeight());

                imageReader = ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 2);
                imageReader.setOnImageAvailableListener(this, handler);

                sensorArraySize = cameraInfo.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                isMeteringAreaAFSupported = cameraInfo.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF) >= 1;

                return cameraId;
            }
        }
        return null;
    }

    private void lockCamera() {
        boolean acquired = false;
        while (!acquired) {
            try {
                cameraOpenCloseLock.acquire();
                acquired = true;
            } catch (InterruptedException e) {}
        }
    }

    private void unlockCamera() {
        cameraOpenCloseLock.release();
    }

    private void openCamera(Context context) throws CameraAccessException {

        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        String cameraId = chooseCamera(cameraManager);

        if (cameraId == null) {
            listener.onError("No suitable camera found on your device.");
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            listener.onError("Access to camera denied, please check application's permissions");
            return;
        }

        lockCamera();
        cameraManager.openCamera(cameraId, cameraStateCallback, handler);
    }

    private void closeCamera() {
        boolean acquired = false;
        try {
            while (!acquired) {
                try {
                    cameraOpenCloseLock.acquire();
                    acquired = true;
                } catch (InterruptedException e) {
                }
            }
            if (null != captureSession) {
                captureSession.close();
                captureSession = null;
            }
            if (null != camera) {
                camera.close();
                camera = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    public void start(Context context, AutoFitTextureView textureView) {
        this.context = context;
        this.textureView = textureView;
        startBackgroundHandler();

        try {
            if (textureView.isAvailable()) {
                openCamera(context);
            } else {
                textureView.setSurfaceTextureListener(this);
            }
        } catch (CameraAccessException e) {
            listener.onError("Error accessing camera.");
        }
    }

    public void stop() {
        closeCamera();
        stopBackgroundHandler();
    }

    /**
     * Tell the camera to focus on the given point.
     * @param x Coordinate of the point between 0 and 1
     * @param y Coordinate of the point between 0 and 1
     * @return true on success, false if not supported.
     */
    public boolean focusOnPoint(float x, float y) {

        boolean result = false;
        //If capture session has not been created
        //(Example : access denied to camera
        if (captureSession == null) {
            return result;
        }

        Log.d(TAG, "Focusing camera manually...");
        lockCamera();
        Log.d(TAG, "Focusing camera manually : locked camera");

        int xReal = (int) (x * sensorArraySize.width());
        int yReal = (int) (y * sensorArraySize.height());

        Log.d(TAG, String.format("Focusing point (%d, %d) on sensor", xReal, yReal));

        MeteringRectangle focusAreaTouch =
                new MeteringRectangle(
                        Math.max(xReal - 15,  0),
                        Math.max(yReal - 15, 0),
                        30,
                        30,
                        MeteringRectangle.METERING_WEIGHT_MAX - 1
                );

        CameraCaptureSession.CaptureCallback captureCallbackHandler = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);

                if (request.getTag() == "FOCUS_TAG") {
                    //the focus trigger is complete -
                    //resume repeating (preview surface will get frames), clear AF trigger
                    try {
                        requestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                        requestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        captureSession.setRepeatingRequest(requestBuilder.build(), null, null);
                        Log.d(TAG, "Focusing camera manually : restarted preview");
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    } finally {
                        unlockCamera();
                        Log.d(TAG, "Focusing camera manually : unlocked camera");
                        Log.d(TAG, "Focusing camera manually : done");
                    }
                }
            }

            @Override
            public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                super.onCaptureFailed(session, request, failure);
                Log.e(TAG, "Manual AF failure: " + failure.toString());
                unlockCamera();
            }
        };

        try {

            Log.d(TAG, "Focusing camera manually : stopping preview");
            captureSession.stopRepeating();

            requestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
            captureSession.capture(requestBuilder.build(), captureCallbackHandler, handler);
            //Now add a new AF trigger with focus region
            if (isMeteringAreaAFSupported) {
                requestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[]{focusAreaTouch});
                result = true;
            }

            requestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            requestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            requestBuilder.setTag("FOCUS_TAG"); //we'll capture this later for resuming the preview

            //then we ask for a single request (not repeating!)
            Log.d(TAG, "Focusing camera manually : sensor is moving");
            captureSession.capture(requestBuilder.build(), captureCallbackHandler, handler);
        } catch (CameraAccessException e) {
            unlockCamera();
            e.printStackTrace();
        }

        return result;
    }

    public void captureImage() {
        Log.d(TAG, "Capturing image...");
        lockCamera();
        Log.d(TAG, "Capturing image : locked camera");

        CameraCaptureSession.CaptureCallback captureCallbackHandler = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);

                if (request.getTag() == "CAPTURE_TAG") {
                    try {
                        Log.d(TAG, "Capturing image : image captured");
                        requestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
                        requestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        captureSession.setRepeatingRequest(requestBuilder.build(), null, null);
                        Log.d(TAG, "Capturing image : restarted preview");
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    } finally {
                        unlockCamera();
                        Log.d(TAG, "Capturing image : unlocked camera");
                        Log.d(TAG, "Capturing image : done");
                    }
                }
            }

            @Override
            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                super.onCaptureFailed(session, request, failure);
                Log.e(TAG, "Image capture failed : " + failure.toString());
                unlockCamera();
            }
        };


        try {
            Log.d(TAG, "Capturing image : stopping preview");
            captureSession.stopRepeating();

            final CaptureRequest.Builder captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());

            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            captureRequestBuilder.setTag("CAPTURE_TAG");

            Log.d(TAG, "Capturing image : sensor is capturing..");
            captureSession.capture(captureRequestBuilder.build(), captureCallbackHandler, handler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
            unlockCamera();
        }
    }

    public CameraProcessor(CameraProcessorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        try {
            openCamera(context);
            configureTransform(width, height);
        } catch (CameraAccessException e) {
            stopBackgroundHandler();
            listener.onError("Error accessing camera.");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        configureTransform(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        //stop();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
