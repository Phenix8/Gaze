package com.ican.gaze.activity.menu;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.ican.gaze.R;
import com.ican.gaze.activity.common.CommonGazeActivity;
import com.ican.gaze.activity.game.AnamorphosisChoiceActivity;

public class MenuActivity extends CommonGazeActivity {

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        AssignImageButtonRedirection(R.id.createGameButton, LobbyActivity.class, true);
        AssignImageButtonRedirection(R.id.joinGameButton, JoinRoomActivity.class, true);
        AssignImageButtonRedirection(R.id.rulesButton, RulesMenuActivity.class, false);
        AssignImageButtonRedirection(R.id.highscoresButton, NicknameActivity.class, false);

        // Boutton de débug pour commencer directement une partie
        Button debugButton = (Button) findViewById(R.id.Debug_game_button);
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), AnamorphosisChoiceActivity.class);
                startActivity(menuOptionActivity);
            }
        });

    }


    // Assigne à l'ImageButton dont l'ID est passé en paramètre
    // une fonction de redirection vers la classe 'redirectionClass'
    private void AssignImageButtonRedirection(int id, final Class redirectionClass, final boolean checkForWifi)
    {
        ImageButton currentButton = (ImageButton) findViewById(id);
        currentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForWifi) {
                    if (!wifiManager.isWifiEnabled()) {
                        showToast("Pease enable wifi");
                        return;
                    }
                }

                Intent menuOptionActivity = new Intent(getApplicationContext(), redirectionClass);
                startActivity(menuOptionActivity);
            }
        });
    }
}
