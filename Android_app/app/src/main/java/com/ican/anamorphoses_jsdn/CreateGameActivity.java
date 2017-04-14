package com.ican.anamorphoses_jsdn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.Game;
import com.ican.anamorphoses_jsdn.network.GameServer;
import com.ican.anamorphoses_jsdn.network.RoomNotifier;

import java.net.InetAddress;

public class CreateGameActivity extends AppCompatActivity implements View.OnClickListener {

    Button createButton;
    EditText gameNameField;

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
                GameServer.create(
                    new RoomNotifier(Common.BROADCAST_MESSAGE, gameName, Common.UDP_PORT),
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
