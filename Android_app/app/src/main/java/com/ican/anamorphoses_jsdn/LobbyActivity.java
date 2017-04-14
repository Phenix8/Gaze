package com.ican.anamorphoses_jsdn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ican.anamorphoses_jsdn.network.GameClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity
        implements GameClient.GameEventListener {

    private ImageButton returnButton = null;
    private ListView playerList = null;

    private ArrayList<String> playersName = new ArrayList<>();
    private ArrayAdapter<String> adapter;

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
        adapter = new ArrayAdapter<>(this, R.layout.list_item, playersName);
        playerList.setAdapter(adapter);

        GameClient client = new GameClient();
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
            final List<String> names = (List<String>) data;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.addAll(names);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
