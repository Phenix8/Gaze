package com.ican.anamorphoses_jsdn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;

import com.ican.anamorphoses_jsdn.R;

public class NicknameActivity extends GazeActivity {

    private ImageButton okButton = null;
    private ImageButton returnButton = null;
    private EditText enteredEditText = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        enteredEditText = (EditText) findViewById(R.id.nickname_editText);
        enteredEditText.setText(getSharedPreferences("main", MODE_PRIVATE).getString("nickname", ""));

        // Evénement de click sur le boutton "Ok"
        okButton = (ImageButton) findViewById(R.id.OkButton);
        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
            String nickname = enteredEditText.getText().toString().trim();

            if (nickname.isEmpty()) {
                showToast("Please enter a nickname");
            } else {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                SaveNickName(nickname);
                startActivity(intent);
            }
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
    private void SaveNickName(String nickname)
    {
        SharedPreferences sharedPref = getSharedPreferences("main",  MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("nickname", nickname);
        editor.commit();
    }


}
