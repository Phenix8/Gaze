package com.ican.anamorphoses_jsdn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.ican.anamorphoses_jsdn.R;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ImageView splashScreenImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // IMAGEVIEW SPLASHSCREEN
        splashScreenImg = (ImageView) findViewById(R.id.splashscreenImg) ;
        splashScreenImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
                String nickname = sharedPref.getString("nickname", "");

                if (nickname.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), NicknameActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
