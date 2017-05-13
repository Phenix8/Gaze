package com.ican.anamorphoses_jsdn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.Server;
import com.ican.anamorphoses_jsdn.control.Manager;
import com.ican.anamorphoses_jsdn.network.RoomNotifier;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.net.InetAddress;

public class CreateGameActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_game_layout);

        createButton = (Button) findViewById(R.id.createButton);
        gameNameField = (EditText) findViewById(R.id.gameName);

        createButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == createButton) {
            String gameName = gameNameField.getText().toString().trim();
            if (gameNameField.getText().toString().trim().length() > 0) {
                RoomNotifier notifier =
                        new RoomNotifier(Common.BROADCAST_MESSAGE, gameName, Common.UDP_PORT);
                notifier.setUncaughtExceptionHandler(handler);
                new Manager(
                    notifier,
                    Common.TCP_PORT,
                    4
                ).start();

                Intent intent = new Intent(this, LobbyActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("serverAddress", InetAddress.getLoopbackAddress());
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        }
    }
}
