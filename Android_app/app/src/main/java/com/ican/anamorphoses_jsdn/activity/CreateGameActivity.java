package com.ican.anamorphoses_jsdn.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ican.anamorphoses_jsdn.AnamorphGameManager;
import com.ican.anamorphoses_jsdn.R;
import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.RoomNotifier;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.net.InetAddress;

public class CreateGameActivity extends GazeActivity implements View.OnClickListener {

    Button createButton;
    EditText gameNameField;

    private final Activity self = this;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {

                new AlertDialog.Builder(self)
                        .setTitle("Wifi disabled")
                        .setMessage("Thank's to connect to a Wifi network.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                self.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            e.printStackTrace();
        }
    };

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_game_layout);

        createButton = (Button) findViewById(R.id.createButton);
        gameNameField = (EditText) findViewById(R.id.gameName);

        createButton.setOnClickListener(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onClick(View v) {
        if (v == createButton) {
            String gameName = gameNameField.getText().toString().trim();
            AnamorphGameManager.setTitleRoom(gameName);

            if (gameNameField.getText().toString().trim().length() > 0) {

                if (!wifiManager.isWifiEnabled()) {
                    showToast("Please enable Wifi");
                    return;
                }

                GameServerService.StartServer(this, Common.TCP_PORT, Common.DEFAULT_MAX_PLAYER,
                        new RoomNotifier(Common.BROADCAST_MESSAGE, gameName, Common.UDP_PORT), null);

                LobbyActivity.isRoomAdmin = true;
                Intent intent = new Intent(this, LobbyActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("serverAddress", InetAddress.getLoopbackAddress());
                intent.putExtras(b);
                startActivity(intent);
                finish();
            } else {
                showToast("Please enter a room name");
            }
        }
    }
}
