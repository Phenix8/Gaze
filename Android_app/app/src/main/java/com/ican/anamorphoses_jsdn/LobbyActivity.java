package com.ican.anamorphoses_jsdn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

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
    private ListView playerList = null;

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayAdapter<Player> adapter;

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

        playerList = (ListView) findViewById(R.id.playerList);
        adapter = new ArrayAdapter<>(this, R.layout.list_item, players);
        playerList.setAdapter(adapter);

        Client client = new Client();
        try {
            client.addGameEventListener(this);
            client.connectServer(AnamorphGameManager.getplayerNickname(), (InetAddress) getIntent().getExtras().getSerializable("serverAddress"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGameEvent(GameEventType type, Object data) {
        if (type == GameEventType.PLAYER_LIST_CHANGED) {
            final List<Player> players = (List<Player>) data;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.addAll(players);
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (type == GameEventType.PLAYER_STATE_CHANGED) {
        }
    }
}
