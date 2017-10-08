package com.bof.gaze.activity.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bof.gaze.R;

import com.bof.gaze.detection.ObjectDetector;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ObjectDetector.getInstance().loadDetectors(this.getAssets(), "detectors");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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

        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
