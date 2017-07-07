package com.ican.gaze.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ican.gaze.R;
import com.ican.gaze.activity.common.CommonGameActivity;
import com.ican.gaze.model.Anamorphosis;
import com.ican.gaze.network.Common;

import java.io.IOException;

public class AnamorphosisChoiceActivity extends CommonGameActivity
        implements View.OnClickListener {

    private ImageButton easyButton = null;
    private ImageButton mediumButton = null;
    private ImageButton hardButton = null;

    private Anamorphosis easyAnamorphosis;
    private Anamorphosis mediumAnamorphosis;
    private Anamorphosis hardAnamorphosis;

    private Anamorphosis currentAnamorphosis;

    private int foundAnamorphosis = 0;

    private void chooseRandomAnamorphosis() {
        easyAnamorphosis = getAnamorphDictionnary().getRandom(Anamorphosis.Difficulty.EASY, true);
        easyButton.setImageResource(easyAnamorphosis.getLargeDrawableImage());

        mediumAnamorphosis = getAnamorphDictionnary().getRandom(Anamorphosis.Difficulty.MEDIUM, true);
        mediumButton.setImageResource(mediumAnamorphosis.getLargeDrawableImage());

        hardAnamorphosis = getAnamorphDictionnary().getRandom(Anamorphosis.Difficulty.HARD, true);
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
        startActivityForResult(intent, Common.VALIDATE_ANAMORPHOSIS_ACTION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Common.VALIDATE_ANAMORPHOSIS_ACTION_CODE) {
            if (resultCode == RESULT_OK) {
                getAnamorphDictionnary().setAlreadyValidated(currentAnamorphosis);
                try {
                    getGameClient().setFound(currentAnamorphosis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            chooseRandomAnamorphosis();
        }
    }
}
