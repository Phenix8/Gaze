package com.ican.anamorphoses_jsdn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import dlibwrapper.DLibWrapper;

import com.ican.anamorphoses_jsdn.camera.CameraFragment;

public class CameraActivity extends Activity
{

    private ImageView targetAnamorphImg = null;
    private ImageView targetAnamorphBg = null;
    private ImageView zoomAnamorphImg = null;

    private ImageButton abandonImg = null;
    private ImageButton cameraImg = null;
    private ImageButton zoomAnamorphBg = null;

    private CameraFragment cameraInstance = null;

    public static boolean hasToCheckGameEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            showMessage(
                    "Infos",
                    DLibWrapper.getInstance().loadDetectors(this.getAssets(), "detectors") +
                    " detecteur(s) chargé(s)."
            );

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

            }
        } catch (Exception e) {
            showMessage("Une erreure est survenue", e.getLocalizedMessage());
        }

        TextView score = (TextView) findViewById(R.id.scoreTxt);
        score.setText(AnamorphGameManager.getplayerNickname()+ ": " + AnamorphGameManager.getCurrentPlayerScore());

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
                        AnamorphGameManager.setTargetAnamorphosis(null);
                        AnamorphGameManager.setCurrentPlayerScore(AnamorphGameManager.getCurrentPlayerScore() < 2 ? 0 : AnamorphGameManager.getCurrentPlayerScore()-1);
                        Intent anamorphosisChoiceActivity = new Intent(getApplicationContext(), AnamorphosisChoiceActivity.class);
                        startActivity(anamorphosisChoiceActivity);
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
                if (v.getId() == R.id.photo_icon) {
                    //cameraInstance.takePicture();
                    hasToCheckGameEnd = true;
                }
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


    @Override
    public void onResume() {
        super.onResume();
        if (hasToCheckGameEnd)
        {
            CheckForGameEnd();
            hasToCheckGameEnd = false;
        }
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

        Anamorphosis currentAnamorphosis =  AnamorphGameManager.getTargetAnamorphosis();

        targetAnamorphImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), currentAnamorphosis.getDrawableImage(), null));
        zoomAnamorphImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), currentAnamorphosis.getLargeDrawableImage(), null));

        if (currentAnamorphosis.getDifficulty() == AnamorphosisDifficulty.HARD) {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_hard, null));
            zoomAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_hard_large, null));
        }
        else if (currentAnamorphosis.getDifficulty() == AnamorphosisDifficulty.MEDIUM) {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_medium, null));
            zoomAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_medium_large, null));
        }
        else {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_easy, null));
            zoomAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_easy_large, null));
        }

        zoomAnamorphBg.setVisibility(View.INVISIBLE);
        zoomAnamorphImg.setVisibility(View.INVISIBLE);
    }


    // Initialise les images de l'appareil photo et du bouton d'annulation
    private void setToolImages()
    {
        // Icône caméra
        cameraImg = (ImageButton) findViewById(R.id.photo_icon);
        cameraImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.photo_button, null));

        // Icône annulation
        abandonImg = (ImageButton) findViewById(R.id.cancel_anamorph_img);
        abandonImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.abandon_button, null));
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


    // Vérifie si la partie en cours est terminée
    public void CheckForGameEnd()
    {
        AnamorphGameManager.setCurrentPlayerScore(AnamorphGameManager.getCurrentPlayerScore() + 1);
        SaveScores();

        if (AnamorphGameManager.getCurrentPlayerScore() < AnamorphGameManager.VICTORY_ANAMORPH_NB)
        {
            //END OF THE GAME

            //Intent anamorphosisChoiceActivity = new Intent(getApplicationContext(), ResultActivity.class);
            //startActivity(anamorphosisChoiceActivity);
        }
    }

    // Save the added nickname to datas
    private void SaveScores()
    {
        SharedPreferences sharedPref = getSharedPreferences("scoresByNicknameFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(AnamorphGameManager.getplayerNickname(), 0);
        editor.commit();
    }
}
