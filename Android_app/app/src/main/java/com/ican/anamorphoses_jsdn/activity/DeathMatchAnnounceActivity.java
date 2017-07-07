package com.ican.anamorphoses_jsdn.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.ican.anamorphoses_jsdn.R;
import com.ican.anamorphoses_jsdn.control.Player;
import com.ican.anamorphoses_jsdn.network.Client;
import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.resource.AnamorphDictionary;
import com.ican.anamorphoses_jsdn.resource.Anamorphosis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 06/07/2017.
 */

public class DeathMatchAnnounceActivity extends GazeActivity implements Client.GameEventListener {

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
                            currentAnamorphosis = AnamorphDictionary.getInstance().getById(Integer.parseInt(anamorpId));
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

        getGameClient().addGameEventListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Common.VALIDATE_ANAMORPHOSIS_ACTION_CODE) {
            if (resultCode == RESULT_OK) {
                AnamorphDictionary.getInstance().setAlreadyValidated(currentAnamorphosis);

                try {
                    getGameClient().setFound(currentAnamorphosis);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
