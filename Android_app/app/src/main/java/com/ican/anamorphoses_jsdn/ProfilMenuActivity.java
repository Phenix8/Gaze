package com.ican.anamorphoses_jsdn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Map;
import java.util.Set;

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
        enteredEditText.setText(AnamorphGameManager.getplayerNickname());
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

        final String[] profils = LoadNickNameList();//{"player_2", "player_3", "player_4", "player_5"};
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
                        AnamorphGameManager.setplayerNickname(selectedProfil);
                        Intent nicknameActivity = new Intent(getApplicationContext(), MenuActivity.class);
                        SaveNickName();
                        startActivity(nicknameActivity);
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
            }
        });

    }

    // Save the added nickname to datas
    private void SaveNickName()
    {
        SharedPreferences sharedPref = getSharedPreferences("scoresByNicknameFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(AnamorphGameManager.getplayerNickname(), 0);
        editor.commit();
    }

    // Lit les nicknames enregistrés et les convertit
    // en tableau de String avec leur score
    private String[] LoadNickNameList()
    {
        SharedPreferences sharedPref = getSharedPreferences("scoresByNicknameFile", Context.MODE_PRIVATE);
        Map<String, ?> scoreByNickname = sharedPref.getAll();
        Set<String> nicknamesSet =  scoreByNickname.keySet();
        String[] nicknamesArray = nicknamesSet.toArray(new String[0]);
        //for(int i=0; i<nicknamesArray.length; i++)
        //    nicknamesArray[i] += ("    " + (Integer.toString(sharedPref.getInt(nicknamesArray[i], -1))) );
        return nicknamesArray;
    }

}
