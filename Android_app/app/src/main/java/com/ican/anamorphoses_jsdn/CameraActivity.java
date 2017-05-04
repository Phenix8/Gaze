package com.ican.anamorphoses_jsdn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import dlibwrapper.DLibWrapper;
import com.ican.anamorphoses_jsdn.camera.Camera2BasicFragment;
import com.ican.anamorphoses_jsdn.camera.CameraFragment;

public class CameraActivity extends Activity
{

    private ImageView targetAnamorphImg = null;
    private ImageView targetAnamorphBg = null;
    private ImageView zoom_anamorph_img = null;

    private ImageButton abandonImg = null;
    private ImageButton cameraImg = null;
    private ImageButton zoom_anamorph_bg = null;


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
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, CameraFragment.newInstance())
                        .commit();

            }
        } catch (Exception e) {
            showMessage("Une erreure est survenue", e.getLocalizedMessage());
        }

        TextView score = (TextView) findViewById(R.id.scoreTxt);
        score.setText("Score: " + AnamorphGameManager.getCurrentPlayerScore());

        // Evénement de click sur l'annulation d'anamorphose
        abandonImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
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

        // Evénement de click sur l'anamorphose cible
        abandonImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (zoom_anamorph_bg.getVisibility() == View.INVISIBLE)
                {
                    zoom_anamorph_bg.setVisibility(View.VISIBLE);
                    zoom_anamorph_img.setVisibility(View.VISIBLE);
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
        zoom_anamorph_img = (ImageView) findViewById(R.id.zoom_anamorph_img);
        zoom_anamorph_bg = (ImageButton) findViewById(R.id.zoom_anamorph_bg);

        Anamorphosis currentAnamorphosis =  AnamorphGameManager.getTargetAnamorphosis();

        targetAnamorphImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), currentAnamorphosis.getDrawableImage(), null));
        targetAnamorphImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), currentAnamorphosis.getLargeDrawableImage(), null));

        if (currentAnamorphosis.getDifficulty() == AnamorphosisDifficulty.HARD) {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_hard, null));
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_hard_large, null));
        }
        else if (currentAnamorphosis.getDifficulty() == AnamorphosisDifficulty.MEDIUM) {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_medium, null));
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_medium_large, null));
        }
        else {
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_easy, null));
            targetAnamorphBg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anamorphose_easy_large, null));
        }

        zoom_anamorph_bg.setVisibility(View.INVISIBLE);
        zoom_anamorph_img.setVisibility(View.INVISIBLE);
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

}
