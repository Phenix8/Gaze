package com.bof.gaze.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bof.gaze.R;
import com.bof.gaze.activity.common.CommonGameActivity;
import com.bof.gaze.model.Anamorphosis;
import com.bof.gaze.network.Common;

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

    private boolean debugMode = false;

    private boolean alreadyCanceled = false;

    private int foundAnamorphosis = 0;

    private void setImage(int viewId, int imageId) {
        ((ImageView) findViewById(viewId)).setImageResource(imageId);
    }

    private void chooseRandomAnamorphosis() {
        easyAnamorphosis = getAnamorphDictionnary().getRandom(Anamorphosis.Difficulty.EASY, true);
        setImage(R.id.anamorphosis_img_1, easyAnamorphosis.getLargeDrawableImage());

        mediumAnamorphosis = getAnamorphDictionnary().getRandom(Anamorphosis.Difficulty.MEDIUM, true);
        setImage(R.id.anamorphosis_img_2, mediumAnamorphosis.getLargeDrawableImage());

        hardAnamorphosis = getAnamorphDictionnary().getRandom(Anamorphosis.Difficulty.HARD, true);
        setImage(R.id.anamorphosis_img_3, hardAnamorphosis.getLargeDrawableImage());
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

        debugMode = getIntent().getBooleanExtra("debug", false);

        chooseRandomAnamorphosis();
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
        intent.putExtra("alreadyCanceled", alreadyCanceled);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivityForResult(intent, Common.VALIDATE_ANAMORPHOSIS_ACTION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Common.VALIDATE_ANAMORPHOSIS_ACTION_CODE) {
            if (!debugMode) {
                switch (resultCode) {
                    case RESULT_OK:
                        alreadyCanceled = false;
                        getAnamorphDictionnary().setAlreadyValidated(currentAnamorphosis);
                        try {
                            getGameClient().setFound(currentAnamorphosis);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case RESULT_CANCELED:
                        alreadyCanceled = true;
                        break;

                    case RESULT_GAME_ENDED:
                        Intent intent = new Intent(getApplicationContext(), LeaderboardActivity.class);
                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                }
            }
            chooseRandomAnamorphosis();
        }
    }
}
