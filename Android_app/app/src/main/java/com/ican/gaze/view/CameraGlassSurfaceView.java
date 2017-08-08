package com.ican.gaze.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ican.gaze.R;

/**
 * Created by root on 08/08/2017.
 */

public class CameraGlassSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private enum FeedbackType {
        FOUND,
        NOT_FOUND,
        FOCUS
    }

    private static Bitmap grid = null;
    private static Bitmap feedbackFound = null;
    private static Bitmap feedbackNotFound = null;
    private static Bitmap feedbackFocus = null;

    private static final int FEEDBACK_DURATION = 1000;

    private static final Paint paint = new Paint();

    private FeedbackType displayedFeedback = null;
    private long displayStartTime = 0;
    private int focusX;
    private int focusY;

    private void init() {
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        if (grid != null) {
            return;
        }

        grid = BitmapFactory.decodeResource(getResources(), R.drawable.grid);
        feedbackFound = BitmapFactory.decodeResource(getResources(), R.drawable.camera_right);
        feedbackNotFound = BitmapFactory.decodeResource(getResources(), R.drawable.camera_wrong);
        feedbackFocus = BitmapFactory.decodeResource(getResources(), R.drawable.camera_focus);
    }

    private static void drawGrid(Canvas canvas) {
        canvas.drawBitmap(grid,
                new Rect(0, 0, grid.getWidth(), grid.getHeight()),
                new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
                paint
        );
    }

    private static void drawOnCenter(Canvas canvas, Bitmap img) {
        canvas.drawBitmap(img,
                canvas.getWidth() / 2 - img.getWidth() / 2,
                canvas.getHeight() / 2 - img.getHeight() / 2,
                paint
        );
    }

    private static void drawFeedbackFound(Canvas canvas) {
        drawOnCenter(canvas, feedbackFound);
    }

    private static void drawFeedbackNotFound(Canvas canvas) {
        drawOnCenter(canvas, feedbackNotFound);
    }

    private static void drawFeedbackFocus(Canvas canvas, int x, int y, float rotationInDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationInDegree, feedbackFocus.getWidth() / 2.0f, feedbackFocus.getHeight() / 2.0f);
        matrix.postTranslate(x - feedbackFocus.getWidth() / 2, y - feedbackFocus.getHeight() / 2);
        canvas.drawBitmap(feedbackFocus, matrix, paint);
    }

    private static void clearCanvas(Canvas canvas){
        canvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        invalidate();

        clearCanvas(canvas);
        drawGrid(canvas);

        if (displayedFeedback == null) {
            return;
        }

        long displayDuration = System.currentTimeMillis() - displayStartTime;

        if (displayDuration > FEEDBACK_DURATION) {
            displayedFeedback = null;
            return;
        }

        switch (displayedFeedback) {
            case FOCUS:
                drawFeedbackFocus(canvas, focusX, focusY,
                    360.0f *
                    (displayDuration > FEEDBACK_DURATION / 2.0f ? displayDuration : -displayDuration)
                    / (float) FEEDBACK_DURATION
                );
                break;

            case NOT_FOUND:
                if ((displayDuration / 120) % 2 == 0) {
                    drawFeedbackNotFound(canvas);
                }
                break;

            case FOUND:
                drawFeedbackFound(canvas);
                break;

            default:
                break;
        }
    }

    public void displayFeedbackFound() {
        displayedFeedback = FeedbackType.FOUND;
        displayStartTime = System.currentTimeMillis();
    }

    public void displayFeedbackNotFound() {
        displayedFeedback = FeedbackType.NOT_FOUND;
        displayStartTime = System.currentTimeMillis();
    }

    public void displayFeedbackFocus(int x, int y) {
        displayedFeedback = FeedbackType.FOCUS;
        focusX = x;
        focusY = y;
        displayStartTime = System.currentTimeMillis();
    }

    public CameraGlassSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraGlassSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraGlassSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CameraGlassSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setWillNotDraw(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
