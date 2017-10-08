package com.bof.gaze.activity.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bof.gaze.R;

import com.bof.gaze.detection.ObjectDetector;

public class HomeActivity extends AppCompatActivity implements View.OnTouchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.activity_home).setOnTouchListener(this);

        ObjectDetector.getInstance().loadDetectors(this.getAssets(), "detectors");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
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

        return false;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
