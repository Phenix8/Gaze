package com.ican.anamorphoses_jsdn.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import dlibwrapper.DLibWrapper;

import com.ican.anamorphoses_jsdn.resource.AnamorphDictionary;
import com.ican.anamorphoses_jsdn.resource.Anamorphosis;
import com.ican.anamorphoses_jsdn.R;
import com.ican.anamorphoses_jsdn.camera.CameraFragment;

import java.io.IOException;

public class CameraActivity extends GazeActivity
{

    private ImageView targetAnamorphImg = null;
    private ImageView targetAnamorphBg = null;
    private ImageView zoomAnamorphImg = null;
    private ImageView backgroundImg = null;

    private ImageButton abandonImg = null;
    private ImageButton cameraImg = null;
    private ImageButton zoomAnamorphBg = null;

    private CameraFragment cameraInstance = null;

    private Anamorphosis currentAnamorphosis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentAnamorphosis = (Anamorphosis) getIntent().getSerializableExtra("anamorphosis");

        try {
            Log.d("dlib", String.format("number of detectors loaded %d", DLibWrapper.getInstance().loadDetectors(this.getAssets(), "detectors")));


            //requestWindowFeature(Window.FEATURE_NO_TITLE);
            //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_camera);

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
                       setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onNotFound() {

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

        // Evénement de clic sur l'annulation d'anamorphose
        abandonImg.setOnClickListener(new View.OnClickListener()
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
        });

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

        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HideTargetAnamorphZoom())
                    return;
                cameraInstance.checkForAnamorphosis(currentAnamorphosis.getDetectorName());
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

        if (currentAnamorphosis.getDifficulty() == AnamorphosisDifficulty.HARD) {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_hardanam, null));
            zoomAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.choos_hard, null));
        }
        else if (currentAnamorphosis.getDifficulty() == AnamorphosisDifficulty.MEDIUM) {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mediumanam, null));
            zoomAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.choose_medium, null));
        }
        else {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_easyanam, null));
            zoomAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.choose_easy, null));
        }

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
