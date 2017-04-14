package com.ican.anamorphoses_jsdn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {


    private Button createGameButton = null;
    private Button joinGameButton = null;
    private Button rulesButton = null;
    private Button highscoresButton = null;
    private ImageButton returnButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        // Boutton "CREATE GAME"
        createGameButton = (Button) findViewById(R.id.createGameButton);
        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), AnamorphosisChoiceActivity.class);
                startActivity(menuOptionActivity);
            }
        });

        // Boutton "JOIN GAME"
        joinGameButton = (Button) findViewById(R.id.joinGameButton);
        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), ClientMenuActivity.class);
                startActivity(menuOptionActivity);
            }
        });

        // Boutton "RULES"
        rulesButton = (Button) findViewById(R.id.rulesButton);
        rulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), RulesMenuActivity.class);
                startActivity(menuOptionActivity);
            }
        });

        // Boutton "HIGHSCORES"
        highscoresButton = (Button) findViewById(R.id.highscoresButton);
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
