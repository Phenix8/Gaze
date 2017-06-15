package com.ican.anamorphoses_jsdn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
                SharedPreferences sharedPref = getSharedPreferences("scoresByNicknameFile", Context.MODE_PRIVATE);
                Map<String, ?> scoreByNickname = sharedPref.getAll();
                if (scoreByNickname == null || scoreByNickname.keySet().size() > 0) {
                    Intent nicknameActivity = new Intent(getApplicationContext(), NicknameActivity.class);
                    startActivity(nicknameActivity);
                }
                else
                {
                    Intent nicknameActivity = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(nicknameActivity);
                }
            }
        });
    }

    /*
    @Override
    public boolean onTouchEvent (MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            Intent nicknameActivity = new Intent(getApplicationContext(), NicknameActivity.class);
            startActivity(nicknameActivity);
        }

        return true;
    }

    */

    @Override
    public void onBackPressed() {
        return;
    }
}
