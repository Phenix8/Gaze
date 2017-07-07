package com.ican.anamorphoses_jsdn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ican.anamorphoses_jsdn.R;
import com.ican.anamorphoses_jsdn.model.Anamorphosis;

import java.io.IOException;

public class AnamorphosisChoiceActivity extends GazeActivity
        implements View.OnClickListener {

    static final int VALIDATE_ANAMORPHOSIS = 1;

    private ImageButton easyButton = null;
    private ImageButton mediumButton = null;
    private ImageButton hardButton = null;

    private Anamorphosis easyAnamorphosis;
    private Anamorphosis mediumAnamorphosis;
    private Anamorphosis hardAnamorphosis;

    private Anamorphosis currentAnamorphosis;

    private int foundAnamorphosis = 0;

    private void chooseRandomAnamorphosis() {
        easyAnamorphosis = getAnamorphDictionnary().getRandom(AnamorphosisDifficulty.EASY, false);
        easyButton.setImageResource(easyAnamorphosis.getLargeDrawableImage());

        mediumAnamorphosis = getAnamorphDictionnary().getRandom(AnamorphosisDifficulty.MEDIUM, false);
        mediumButton.setImageResource(mediumAnamorphosis.getLargeDrawableImage());

        hardAnamorphosis = getAnamorphDictionnary().getRandom(AnamorphosisDifficulty.HARD, false);
        hardButton.setImageResource(hardAnamorphosis.getLargeDrawableImage());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anamorphose_choice);

        easyButton = (ImageButton) findViewById(R.id.easyImgButton);
        easyButton.setOnClickListener(this);

        mediumButton = (ImageButton) findViewById(R.id.mediumImgButton);
        mediumButton.setOnClickListener(this);

        hardButton = (ImageButton) findViewById(R.id.hardImgButton);
        hardButton.setOnClickListener(this);

        chooseRandomAnamorphosis();

        getGameClient().addGameEventListener(this);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View view) {
        currentAnamorphosis = null;

        if (view == easyButton) {
            currentAnamorphosis = easyAnamorphosis;
        } else if (view == mediumButton) {
            currentAnamorphosis = mediumAnamorphosis;
        } else if (view == hardButton) {
            currentAnamorphosis = hardAnamorphosis;
        }

        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("anamorphosis", currentAnamorphosis);
        startActivityForResult(intent, VALIDATE_ANAMORPHOSIS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VALIDATE_ANAMORPHOSIS) {
            chooseRandomAnamorphosis();
            if (resultCode == RESULT_OK) {
                getAnamorphDictionnary().setAlreadyValidated(currentAnamorphosis);
                try {
                    getGameClient().setFound(currentAnamorphosis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
