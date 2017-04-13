package com.ican.anamorphoses_jsdn;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HighscoresMenuActivity extends AppCompatActivity {

    private ListView scoresList = null;
    private ImageButton returnButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores_menu);

        //////////////
        // LISTVIEW //
        //////////////

        // Remplissage de la liste de noms déjà entrés
        scoresList = (ListView) findViewById(R.id.scores_listView);

        final String[] names = {"player_2", "player_3", "player_4", "player_5"};
        ArrayAdapter<String> scoresAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, names);
        scoresList.setAdapter(scoresAdapter);

        ///////////////////////////
        // IMAGE BUTTON "Return" //
        ///////////////////////////

        // Evénement de click sur le boutton "Return"
        returnButton = (ImageButton)findViewById(R.id.returnImgButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                // retour
                Log.i("Finish", "Finish nickname activity");
                finish();
            }
        });

    }

    // Fonction de chargement du fichier de scores
    public boolean AddNicknameToFile(String newNickname)
    {
        FileOutputStream output = null;
        try {
            output = openFileOutput("NickNames", MODE_PRIVATE);
            output.write(newNickname.getBytes());
            if(output != null)
                output.close();

            return true;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
