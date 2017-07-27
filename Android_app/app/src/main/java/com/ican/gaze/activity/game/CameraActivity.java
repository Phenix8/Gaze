package com.ican.gaze.activity.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dlibwrapper.DLibWrapper;

import com.ican.gaze.activity.common.CommonGameActivity;
import com.ican.gaze.model.Anamorphosis;
import com.ican.gaze.R;
import com.ican.gaze.camera.CameraFragment;

import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends CommonGameActivity
{
    private static final String[] tips = {
            "Make sure you have framed the anamorphosis in the center of the screen",
            "Make sure to take the anamorphosis in the right sens"
    };

    private ImageView targetAnamorphImg = null;
    private ImageView targetAnamorphBg = null;
    private ImageView zoomAnamorphImg = null;
    private ImageView backgroundImg = null;

    private ImageButton abandonImg = null;
    private ImageButton cameraImg = null;
    private ImageButton zoomAnamorphBg = null;
    private ImageButton cameraGrid = null;

    private CameraFragment cameraInstance = null;

    private Anamorphosis currentAnamorphosis;

    private MediaPlayer sonObturateur;

    private int tipIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sonObturateur = MediaPlayer.create(getApplicationContext(), R.raw.obturateur);

        currentAnamorphosis = (Anamorphosis) getIntent().getSerializableExtra("anamorphosis");

        try {
            Log.d("dlib", String.format("number of detectors loaded %d", DLibWrapper.getInstance().loadDetectors(this.getAssets(), "detectors")));


            //requestWindowFeature(Window.FEATURE_NO_TITLE);
            //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_camera);

            cameraGrid = (ImageButton) findViewById(R.id.camera_background);
            cameraGrid.setImageResource(R.drawable.camera);

            setTargetAnamorphImg();
            setToolImages();

            if (null == savedInstanceState) {
                cameraInstance = CameraFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, cameraInstance)
                        .commit();

                CameraFragment.ImageTester.setCallback(new CameraFragment.ImageTester.Callback() {
                        @Override
                        public void onFound() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cameraGrid.setImageResource(R.drawable.camera_right);
                            }
                        });
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onNotFound() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cameraGrid.setImageResource(R.drawable.camera_wrong);
                            }
                        });
                        Timer t = new Timer();
                        t.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cameraGrid.setImageResource(R.drawable.camera);
                                    }
                                });
                            }
                        }, 1000);
                        showToast(tips[tipIndex], Toast.LENGTH_LONG);
                        tipIndex = (tipIndex + 1) % tips.length;
                    }

                    @Override
                    public void onError(String message) {
                        showMessage("Error processing image :", message);
                        Log.d("dlib", "Test3");
                    }
                });
            }

        } catch (Exception e) {
            showMessage("Une erreure est survenue", e.getLocalizedMessage());
        }

        TextView score = (TextView) findViewById(R.id.scoreTxt);
        score.setText(String.format("Score : %d",getGameClient().getScore()));

        final View.OnClickListener cancelListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (HideTargetAnamorphZoom())
                    return;

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you want to abandon this anamorphosis ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id)
                    {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
            }
        };

        if (getIntent().getBooleanExtra("alreadyCanceled", false)) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            abandonImg.setImageResource(R.drawable.camera_cancel);
                            abandonImg.setOnClickListener(cancelListener);
                        }
                    });
                }
            }, 30000);
            abandonImg.setImageResource(R.drawable.camera_cancel_disabled);
            abandonImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showToast(String.format("Please wait a few seconds..."));
                }
            });
        } else {
            abandonImg.setOnClickListener(cancelListener);
        }

        // Evénement de clic sur l'anamorphose cible
        targetAnamorphBg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (HideTargetAnamorphZoom())
                    return;
                else if (zoomAnamorphBg.getVisibility() == View.INVISIBLE)
                {
                    zoomAnamorphBg.setVisibility(View.VISIBLE);
                    zoomAnamorphImg.setVisibility(View.VISIBLE);
                }
            }
        });

        // Evénement de clic sur l'anamorphose zoomée
        zoomAnamorphBg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { HideTargetAnamorphZoom();  }
        });

        cameraImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int actionMasked = motionEvent.getActionMasked();
                if (actionMasked != MotionEvent.ACTION_DOWN) {
                    return true;
                }
                if (HideTargetAnamorphZoom())
                    return true;
                sonObturateur.start();
                cameraInstance.checkForAnamorphosis(currentAnamorphosis.getDetectorName());
                return true;
            }
        });

        cameraGrid.setClickable(false);
        cameraGrid.setFocusable(false);

        findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int actionMasked = motionEvent.getActionMasked();
                if (actionMasked != MotionEvent.ACTION_DOWN) {
                    return false;
                }

                float x = motionEvent.getX() / (float) view.getWidth();
                float y = motionEvent.getY() / (float) view.getHeight();
                Log.d("Camera", String.format("Touched point (%f, %f)", x, y));

                cameraInstance.setFocusPoint(x, y);

                return false;
            }
        });
    }

    public void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        HideTargetAnamorphZoom();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    // Initialise l'image et la couleur de background
    // de l'anamorphose cible
    private void setTargetAnamorphImg()
    {
        // icone anamorphse cible
        targetAnamorphImg = (ImageView) findViewById(R.id.target_anamorph_img);
        targetAnamorphBg = (ImageButton) findViewById(R.id.target_anamorph_bg);

        // anamorphose cible zommée
        zoomAnamorphImg = (ImageView) findViewById(R.id.zoom_anamorph_img);
        zoomAnamorphBg = (ImageButton) findViewById(R.id.zoom_anamorph_bg);

        targetAnamorphImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), currentAnamorphosis.getDrawableImage(), null));
        zoomAnamorphImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), currentAnamorphosis.getLargeDrawableImage(), null));

        zoomAnamorphBg.setVisibility(View.INVISIBLE);
        zoomAnamorphImg.setVisibility(View.INVISIBLE);
    }


    // Initialise les images de l'appareil photo et du bouton d'annulation + du background
    private void setToolImages()
    {
        // Background
        backgroundImg = (ImageView) findViewById(R.id.camera_background);
        backgroundImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.camera, null));
        // Icône caméra
        cameraImg = (ImageButton) findViewById(R.id.photo_icon);
        cameraImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_pictureicon, null));

        // Icône annulation
        abandonImg = (ImageButton) findViewById(R.id.cancel_anamorph_img);
        abandonImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_cancel, null));
    }

    // Dissimuler l'anamorphose zoomée
    private boolean HideTargetAnamorphZoom()
    {
        if (zoomAnamorphBg.getVisibility() == View.VISIBLE)
        {
            zoomAnamorphBg.setVisibility(View.INVISIBLE);
            zoomAnamorphImg.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }



}
