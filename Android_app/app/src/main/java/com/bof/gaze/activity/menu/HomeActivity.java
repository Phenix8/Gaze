package com.bof.gaze.activity.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bof.gaze.R;
import com.bof.gaze.detection.ObjectDetector;

public class HomeActivity extends AppCompatActivity implements View.OnTouchListener {

    private TextView permissionTxt;
    private Button permissionBtn;
    private FrameLayout permissionPopup;

    int WIFI_PERMISSIONS_REQUEST = 1, CAMERA_PERMISSIONS_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.activity_home).setOnTouchListener(this);

        /*
        // Permission pop-up
        permissionTxt = (TextView) findViewById(R.id.homePermissionTxt);
        permissionPopup = (FrameLayout) findViewById(R.id.homePermissionLayout);

        permissionBtn = (Button) findViewById(R.id.homePermissionBtn);
        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionPopup.setVisibility(View.INVISIBLE);
                SystemPermissionRequest(permissionBtn.getText() == "Enable Camera");
            }
        });
        */

        ObjectDetector.getInstance().loadDetectors(this.getAssets(), "detectors");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        SharedPreferences sharedPref = getSharedPreferences("main", Context.MODE_PRIVATE);
        String nickname = sharedPref.getString("nickname", "");

        if (nickname.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), NicknameActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.setFlags(intent.getFlags());
            startActivity(intent);
        }

        return false;
    }

    // Vérification of WIFI and CAMERA permissions
    public boolean CheckForPersmissions()
    {

        boolean result = false;

        // Permission to use the "WiFi" component
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE))
            {
                permissionTxt.setText("The Wifi permission is required to play");
                permissionBtn.setText("Enable Wifi");
                permissionPopup.setVisibility(View.VISIBLE);
            }
            else
                SystemPermissionRequest(false);
        }
        else
            result = true;

        // Permission to use the "Camera" component
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {
                permissionTxt.setText("The Camera permission is required to play");
                permissionBtn.setText("Enable Camera");
                permissionPopup.setVisibility(View.VISIBLE);
            }
            else
                SystemPermissionRequest(true);
            return false;
        }
        else
            result &= true;

        return result;
    }

    private void SystemPermissionRequest(boolean isForCamera)
    {
        if (isForCamera)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, WIFI_PERMISSIONS_REQUEST);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
