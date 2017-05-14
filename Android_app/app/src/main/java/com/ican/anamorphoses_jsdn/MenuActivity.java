package com.ican.anamorphoses_jsdn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {


    private ImageButton createGameButton = null;
    private ImageButton joinGameButton = null;
    private ImageButton rulesButton = null;
    private ImageButton highscoresButton = null;
    private ImageButton returnButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        // Boutton "CREATE GAME"
        createGameButton = (ImageButton) findViewById(R.id.createGameButton);
        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), CreateGameActivity.class);
                startActivity(menuOptionActivity);
            }
        });

        // Boutton "JOIN GAME"
        joinGameButton = (ImageButton) findViewById(R.id.joinGameButton);
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), JoinRoomActivity.class);
                startActivity(menuOptionActivity);
            }
        });

        // Boutton "RULES"
        rulesButton = (ImageButton) findViewById(R.id.rulesButton);
        rulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), AnamorphosisChoiceActivity.class);
                startActivity(menuOptionActivity);
            }
        });

        // Boutton "HIGHSCORES"
        highscoresButton = (ImageButton) findViewById(R.id.highscoresButton);
        highscoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), ProfilMenuActivity.class);
                startActivity(menuOptionActivity);
            }
        });

        ///////////////////////////
        // IMAGE BUTTON "Return" //
        ///////////////////////////

        // Evénement de click sur le boutton "Return"
        returnButton = (ImageButton) findViewById(R.id.returnImgButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
