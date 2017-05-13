package com.ican.anamorphoses_jsdn;

import android.app.Dialog;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.EditText;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class NicknameActivity extends AppCompatActivity {

    private ImageButton okButton = null;
    private ImageButton returnButton = null;
    private EditText enteredEditText = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

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
                Builder builder = new AlertDialog.Builder(v.getContext());
                enteredEditText = (EditText) findViewById(R.id.nickname_editText);
                builder.setMessage("Do you choose : " + enteredEditText.getText().toString() + " ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id)
                    {
                        AnamorphGameManager.setplayerNickname(enteredEditText.getText().toString());
                        Intent nicknameActivity = new Intent(getApplicationContext(), MenuActivity.class);
                        SaveNickName();
                        startActivity(nicknameActivity);
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
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


    // Save the added nickname to datas
    private void SaveNickName()
    {
        SharedPreferences sharedPref = getSharedPreferences("scoresByNicknameFile",  MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(AnamorphGameManager.getplayerNickname(), 0);
        editor.commit();
    }


}
