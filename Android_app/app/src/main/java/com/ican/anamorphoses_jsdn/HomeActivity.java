package com.ican.anamorphoses_jsdn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.GameClient;
import com.ican.anamorphoses_jsdn.network.GameServer;
import com.ican.anamorphoses_jsdn.network.RoomFinder;
import com.ican.anamorphoses_jsdn.network.RoomNotifier;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Button touchToStart = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

            // Orientation de l'écran - faite dans le XML pour l'instant
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //Screen.lockOrientation(this);
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            Intent nicknameActivity = new Intent(getApplicationContext(), NicknameActivity.class);
            startActivity(nicknameActivity);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
