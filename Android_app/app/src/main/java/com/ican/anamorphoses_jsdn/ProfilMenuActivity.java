package com.ican.anamorphoses_jsdn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfilMenuActivity extends AppCompatActivity {

    private ListView profilsList = null;
    private EditText enteredEditText = null;
    private ImageButton returnButton = null;
    private ImageButton okButton = null;
    private String selectedProfil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_menu);

        //////////////
        // EDITTEXT //
        //////////////

        enteredEditText = (EditText) findViewById(R.id.nickname_editText);
        enteredEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    profilsList.clearChoices();
                    profilsList.requestLayout();
                    profilsList.refreshDrawableState();
                    selectedProfil = enteredEditText.getText().toString();
                }
                else
                    selectedProfil = enteredEditText.getText().toString();
            }
        });

        //////////////
        // LISTVIEW //
        //////////////

        // Remplissage de la liste de noms déjà entrés
        profilsList = (ListView) findViewById(R.id.nicknames_listView);

        final String[] profils = {"player_2", "player_3", "player_4", "player_5"};
        //final String[] names = LoadNicknames();

        ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, profils);
        profilsList.setAdapter(nicknamesAdapter);
        profilsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Fonction de clic sur un item de la liste
        profilsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedProfil = profils[position];
                profilsList.setItemChecked(position, true);
            }
        });

        ///////////////////////////
        // IMAGE BUTTON "Return" //
        ///////////////////////////

        // Evénement de click sur le boutton "Return"
        returnButton = (ImageButton)findViewById(R.id.returnImgButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                finish();
            }
        });

        ///////////////////////
        // IMAGE BUTTON "Ok" //
        ///////////////////////

        // Evénement de click sur le boutton "Ok"
        okButton = (ImageButton) findViewById(R.id.OkButton);
        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Récupération du nickname choisi dans la liste

                // Confirmation du nickname choisi
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you choose : " + selectedProfil + " ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id)
                    {
                        AnamorphGameManager.setplayerNickname(enteredEditText.getText().toString());
                        Intent nicknameActivity = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(nicknameActivity);
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
            }
        });

    }

    // Fonction de chargement du fichier de scores
    public boolean AddProfilToFile(String newNickname)
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

    // Fonction de chargement du fichier de profils
    public String[] LoadProfils()
    {
        FileInputStream input = null;
        String[] resultList = null;

        try {
            input = openFileInput("NickNames");

            while ((input.read()) != -1) {
                input.read();
            }
            if(input != null)
                input.close();

            return resultList;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
