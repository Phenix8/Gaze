package com.ican.anamorphoses_jsdn;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.EditText;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class NicknameActivity extends AppCompatActivity {

    private Button okButtonTouchAndClick = null;
    private ListView nicknamesList = null;
    private String selectedNickname = null;
    private EditText enteredEditText = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        //////////////
        // LISTVIEW //
        //////////////

        // Remplissage de la liste de noms déjà entrés
        nicknamesList = (ListView) findViewById(R.id.nicknames_listView);
        final String[] names = {"player_2", "player_3", "player_4", "player_5"};
        ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, names);
        nicknamesList.setAdapter(nicknamesAdapter);
        nicknamesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Fonction de clic sur un item de la liste
        nicknamesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedNickname = names[position];
                nicknamesList.setItemChecked(position, true);
            }
        });

        //////////////
        // EDITTEXT //
        //////////////

        enteredEditText = (EditText) findViewById(R.id.nickname_editText);
        enteredEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean hasFocus) {
                 Log.i("Tett", String.format("EditText has received focus ? {0}", hasFocus));
                    if (hasFocus) {
                        nicknamesList.clearChoices();
                        nicknamesList.requestLayout();

                        Log.i("Test", "ListView a de selectionné : " + (String.valueOf(nicknamesList.getSelectedItemPosition())));
                    }
             }
         });

        /////////////////
        // BUTTON "Ok" //
        /////////////////

                // Evénement de click sur le boutton "Ok"
                okButtonTouchAndClick = (Button) findViewById(R.id.OkButton);
        okButtonTouchAndClick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Récupération du nickname choisi dans la liste

                Log.i("Test", "nicknamesList.getSelectedItem() = " + nicknamesList.getSelectedItem());
                if (nicknamesList.getSelectedItem() == null)
                    selectedNickname = enteredEditText.getText().toString();

                // Confirmation du nickname choisi
                Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Do you choose : " + selectedNickname + " ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // Redirection
                        Intent nicknameActivity = new Intent(getApplicationContext(), CameraScanActivity.class);
                        startActivity(nicknameActivity);
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();


            }
        });
    }
}
