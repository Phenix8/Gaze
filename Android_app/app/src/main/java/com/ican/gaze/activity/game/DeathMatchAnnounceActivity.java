package com.ican.gaze.activity.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.ican.gaze.R;
import com.ican.gaze.activity.common.CommonGameActivity;
import com.ican.gaze.network.Common;
import com.ican.gaze.model.Anamorphosis;

import java.io.IOException;

/**
 * Created by root on 06/07/2017.
 */

public class DeathMatchAnnounceActivity extends CommonGameActivity {

    private TextView deathMatchAnnounceText;

    private Anamorphosis currentAnamorphosis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deathMatchAnnounceText = (TextView) findViewById(R.id.deathMatchText);

        final String anamorpId = getIntent().getExtras().getString("id");

        if (!anamorpId.isEmpty()) {
            deathMatchAnnounceText.setText("You are tied with another player.. You will have to find a last anamorphosis");
            final Activity self = this;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        Intent intent = new Intent(self, CameraActivity.class);
                        try {
                            currentAnamorphosis = getAnamorphDictionnary().getById(Integer.parseInt(anamorpId));
                            intent.putExtra("anamorphosis", currentAnamorphosis);
                            startActivityForResult(intent, Common.VALIDATE_ANAMORPHOSIS_ACTION_CODE);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {}
                }
            }.start();
        } else {
            deathMatchAnnounceText.setText("Two players are tied.. Please wait while they are separating");
        }
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
        }
    }
}
