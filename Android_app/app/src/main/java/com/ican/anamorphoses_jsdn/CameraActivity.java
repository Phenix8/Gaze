package com.ican.anamorphoses_jsdn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import dlibwrapper.DLibWrapper;
import com.ican.anamorphoses_jsdn.camera.Camera2BasicFragment;
import com.ican.anamorphoses_jsdn.camera.CameraFragment;

public class CameraActivity extends Activity {

    private ImageButton targetAnamorphImg = null;


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

            if (null == savedInstanceState) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, CameraFragment.newInstance())
                        .commit();
            }
        } catch (Exception e) {
            showMessage("Une erreure est survenue", e.getLocalizedMessage());
        }

        /*
        // Evénement de click sur l'anamorphose cible
        targetAnamorphImg = (ImageButton) findViewById(R.id.targetAnamorphImg);
        targetAnamorphImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Intent anamorphosisChoiceActivity = new Intent(getApplicationContext(), AnamorphosisChoiceActivity.class);
                startActivity(anamorphosisChoiceActivity);
            }

        });
        */
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
}
