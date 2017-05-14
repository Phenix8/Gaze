package com.ican.anamorphoses_jsdn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.Intent;

import com.ican.anamorphoses_jsdn.control.Player;
import com.ican.anamorphoses_jsdn.network.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity
        implements Client.GameEventListener {

    private ImageButton returnButton = null;
    private ImageButton readyButton = null;
    private ListView playerList = null;

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayAdapter<Player> adapter;

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity_layout);

        ///////////////////////////
        // IMAGE BUTTON "Return" //
        ///////////////////////////

        // Evénement de click sur le boutton "Return"
        returnButton = (ImageButton)findViewById(R.id.returnImgButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
            // retour
            Log.i("Finish", "Finish nickname activity");
            finish();
        }
        });

        readyButton = (ImageButton) findViewById(R.id.readyImgButton);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    client.toggleReady();
                } catch (IOException e) {
                    showError("A network error occured.");
                }
            }
        });

        playerList = (ListView) findViewById(R.id.playerList);
        adapter = new ArrayAdapter<>(this, R.layout.list_item, players);
        playerList.setAdapter(adapter);

        client = new Client();
        try {
            client.addGameEventListener(this);
            client.connectServer(
                    AnamorphGameManager.getplayerNickname(),
                    (InetAddress) getIntent().getExtras().getSerializable("serverAddress"));
        } catch (UnknownHostException e) {
            showError("Server address is invalid.");
        } catch (IOException e) {
            showError("A network error occured");
        }
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onGameEvent(GameEventType type, Object data) {
        final GameEventType t = type;
        final Object d = data;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (t == GameEventType.PLAYER_LIST_CHANGED) {
                    adapter.clear();
                    adapter.addAll((List<Player>) d);
                    adapter.notifyDataSetChanged();
                } else if (t == GameEventType.GAME_STARTED) {
                    Intent intent =
                        new Intent(getApplicationContext(),
                            AnamorphosisChoiceActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("client", client);
                    intent.putExtras(b);
                    startActivity(intent);
                } else if (t == GameEventType.ERROR_OCCURED) {
                    showError((String) d);
                }
            }
        });
    }
}
