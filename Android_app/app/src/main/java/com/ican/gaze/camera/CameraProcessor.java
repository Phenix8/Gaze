package com.ican.gaze.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

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

public class CameraProcessor implements TextureView.SurfaceTextureListener {

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public interface CameraErrorHandler {
        void onError(String error);
    }

    private CameraErrorHandler errorHandler;

    private Context context;
    private AutoFitTextureView textureView;
    private ImageReader imageReader;

    private HandlerThread handlerThread;
    private Handler handler;

    private CameraDevice camera;
    private CameraCaptureSession captureSession;
    private CaptureRequest previewCaptureRequest;

    private Semaphore cameraOpenCloseLock = new Semaphore(1);

    private Size optimalSize = null;

    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            try {
                camera = cameraDevice;
                createCaptureSession(cameraDevice);
            } catch (CameraAccessException e) {
                errorHandler.onError("Error configuring camera");
                e.printStackTrace();
            } finally {
                cameraOpenCloseLock.release();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {

        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            errorHandler.onError("Error accessing camera.");
        }
    };

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
            Log.e("TestCamera", "Couldn't find any suitable preview size");
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

        final CaptureRequest.Builder requestBuilder =
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

                    previewCaptureRequest = requestBuilder.build();
                    captureSession.setRepeatingRequest(previewCaptureRequest,
                            captureCallback, handler);
                } catch (CameraAccessException e) {
                    errorHandler.onError("An error occured while configuring preview.");
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                errorHandler.onError("An error occured while configuring preview.");
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

                textureView.setAspectRatio(optimalSize.getWidth(), optimalSize.getHeight());
                textureView.getSurfaceTexture().setDefaultBufferSize(optimalSize.getWidth(), optimalSize.getHeight());
                imageReader = ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 2);

                return cameraId;
            }
        }
        return null;
    }

    private void openCamera(Context context) throws CameraAccessException {

        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        String cameraId = chooseCamera(cameraManager);

        if (cameraId == null) {
            errorHandler.onError("No suitable camera found on your device.");
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {

            }
        }, handler);

        boolean acquired = false;
        while (!acquired) {
            try {
                cameraOpenCloseLock.acquire();
                acquired = true;
            } catch (InterruptedException e) {}
        }
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
            errorHandler.onError("Error accessing camera.");
        }
    }

    public void stop() {
        closeCamera();
        stopBackgroundHandler();
    }

    public CameraProcessor(CameraErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        try {
            openCamera(context);
            configureTransform(width, height);
        } catch (CameraAccessException e) {
            stopBackgroundHandler();
            errorHandler.onError("Error accessing camera.");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        configureTransform(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
