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
import com.ican.anamorphoses_jsdn.network.Client;

import java.io.IOException;

public class CameraActivity extends Activity
{

    private ImageView targetAnamorphImg = null;
    private ImageView targetAnamorphBg = null;
    private ImageView zoomAnamorphImg = null;
    private ImageView backgroundImg = null;

    private ImageButton abandonImg = null;
    private ImageButton cameraImg = null;
    private ImageButton zoomAnamorphBg = null;

    private CameraFragment cameraInstance = null;

    private Client gameClient;

    public static boolean hasToCheckGameEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameClient = (Client) getIntent().getSerializableExtra("client");

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
                    cameraInstance.takePicture();
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


    // Vérifie si la partie en cours est terminée
    public void CheckForGameEnd()
    {
        AnamorphGameManager.setCurrentPlayerScore(AnamorphGameManager.getCurrentPlayerScore() + 1);
        SaveScores();

        switch (AnamorphGameManager.getTargetAnamorphosis().getDifficulty()) {
            case EASY:
                gameClient.incrementScore(20);
            break;

            case MEDIUM:
                gameClient.incrementScore(30);
            break;

            case HARD:
                gameClient.incrementScore(40);
            break;
        }

        if (AnamorphGameManager.getCurrentPlayerScore() < AnamorphGameManager.VICTORY_ANAMORPH_NB)
        {
            //END OF THE GAME

            try {
                gameClient.annouceAllFound();
            } catch (IOException e) {
                showMessage("Error", "A network error occured.");
            }
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
