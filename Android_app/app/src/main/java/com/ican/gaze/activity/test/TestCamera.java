package com.ican.gaze.activity.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.ican.gaze.R;
import com.ican.gaze.activity.common.CommonGameActivity;
import com.ican.gaze.camera.AutoFitTextureView;
import com.ican.gaze.camera.CameraProcessor;
import com.ican.gaze.model.Anamorphosis;

import java.util.Timer;
import java.util.TimerTask;

public class TestCamera extends CommonGameActivity
        implements View.OnClickListener, CameraProcessor.CameraErrorHandler, View.OnTouchListener {

    private ImageView cancelImg, littleAnamorphImg, largeAnamorphImg, cameraImg, gridImg;
    private AutoFitTextureView textureView;

    private Anamorphosis targetAnamorphosis;

    private boolean canCancel = true;

    private CameraProcessor cameraProcessor = new CameraProcessor(this);

    private void loadComponents() {
        cancelImg = (ImageView) findViewById(R.id.camera_act_cancel_img);
        cancelImg.setOnClickListener(this);

        littleAnamorphImg = (ImageView) findViewById(R.id.camera_act_little_anamorph_img);
        littleAnamorphImg.setOnClickListener(this);

        largeAnamorphImg = (ImageView) findViewById(R.id.camera_act_large_anamorph_img);
        largeAnamorphImg.setOnClickListener(this);

        cameraImg = (ImageView) findViewById(R.id.camera_act_camera_img);
        cameraImg.setOnClickListener(this);

        gridImg = (ImageView) findViewById(R.id.camera_act_grid_img);
        gridImg.setOnTouchListener(this);

        textureView = (AutoFitTextureView) findViewById(R.id.camera_act_surface);
    }

    private void loadTargetAnamorphosis() {
        targetAnamorphosis = (Anamorphosis) getIntent().getSerializableExtra("anamorphosis");

        if (targetAnamorphosis == null) {
            showToast("Error : no target anamorphosis defined");
            return;
        }

        littleAnamorphImg.setImageResource(targetAnamorphosis.getDrawableImage());
        largeAnamorphImg.setImageResource(targetAnamorphosis.getLargeDrawableImage());
    }

    private void cancelAnamorphosis(){
        if (!canCancel) {
            showToast("Please wait a few more seconds");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you  really want to try another anamorphosis ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void searchTargetAnamorphosis() {

    }

    private void toggleLargeAnamorphosisImg() {
        View v = (View) largeAnamorphImg.getParent();
        v.setVisibility(v.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
    }

    private void checkForAlreadyCanceledState() {
        if (!getIntent().getBooleanExtra("alreadyCanceled", false) ||
                getIntent().getBooleanExtra("debug", false)) {
            return;
        }

        canCancel = false;
        cancelImg.setImageResource(R.drawable.camera_cancel_disabled);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        canCancel = true;
                        cancelImg.setImageResource(R.drawable.camera_cancel);
                    }
                });
            }
        }, 30000);
    }

    @Override
    public void onError(String error) {
        showError(error);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (view == gridImg) {
                cameraProcessor.focusOnPoint(
                        motionEvent.getX() / (float) view.getWidth(),
                        motionEvent.getY() / (float) view.getHeight()
                );
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        if (view == cancelImg) {
            cancelAnamorphosis();
        } else if (view == littleAnamorphImg){
            toggleLargeAnamorphosisImg();
        } else if (view == largeAnamorphImg) {
            toggleLargeAnamorphosisImg();
        } else if (view == cameraImg) {
            searchTargetAnamorphosis();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_camera);

        loadComponents();
        loadTargetAnamorphosis();
        checkForAlreadyCanceledState();
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraProcessor.start(this, textureView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraProcessor.stop();
    }
}
