package com.ican.anamorphoses_jsdn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        AssignImageButtonRedirection(R.id.createGameButton, CreateGameActivity.class);
        AssignImageButtonRedirection(R.id.joinGameButton, JoinRoomActivity.class);
        AssignImageButtonRedirection(R.id.rulesButton, RulesMenuActivity.class);
        AssignImageButtonRedirection(R.id.highscoresButton, ProfilMenuActivity.class);

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
    private void AssignImageButtonRedirection(int id, final Class redirectionClass)
    {
        ImageButton currentButton = (ImageButton) findViewById(id);
        currentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOptionActivity = new Intent(getApplicationContext(), redirectionClass);
                startActivity(menuOptionActivity);
            }
        });
    }
}
