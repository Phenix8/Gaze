package com.bof.gaze.activity.menu;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bof.gaze.R;
import com.bof.gaze.activity.common.CommonGazeActivity;
import com.bof.gaze.activity.game.AnamorphosisChoiceActivity;
import com.bof.gaze.model.Player;
import com.bof.gaze.network.client.Client;
import com.bof.gaze.network.client.ClientServerSynchronizer;
import com.bof.gaze.network.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends CommonGazeActivity
        implements Client.GameEventListener {

    private ArrayAdapter<Player> adapter;

    private InetAddress serverAddress = null;

    private TextView titleView;

    private class CustomAdapter extends ArrayAdapter<Player> {
        CustomAdapter(Context context, ArrayList<Player> players) {
            super(context, 0, players);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            Player player = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.lobby_player_list_item, parent, false);
            }
            // Lookup view for data population
            TextView tvPlayerName = convertView.findViewById(R.id.playerName);
            ImageView ivPlayerReadyState = convertView.findViewById(R.id.playerReadyState);
            if (player == null) {
                return convertView;
            }
            // Populate the data into the template view using the data object
            tvPlayerName.setText(player.getName());
            ivPlayerReadyState
                .setImageResource(
                    player.isReady() ?
                        R.drawable.room_playerready : R.drawable.room_playernotready);

            if (player.getPlayerId().equals(getGameClient().getPlayerId())){
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            getGameClient().toggleReady();
                        } catch (IOException e) {
                            showError(e.getLocalizedMessage());
                        }
                    }
                });
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_activity_layout);

        ///////////////////////////
        // IMAGE BUTTON "Return" //
        ///////////////////////////

        // Evénement de click sur le boutton "Return"
        ImageButton returnButton = (ImageButton)findViewById(R.id.returnImgButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
            stopServer();
            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
        }
        });

        getGameClient().setGameEventListener(this);

        ListView playerList = (ListView) findViewById(R.id.playerList);
        adapter = new CustomAdapter(this, new ArrayList<Player>());
        playerList.setAdapter(adapter);

        String playerName = getSharedPreferences("main", MODE_PRIVATE).getString("nickname", "unknown player");

        Bundle b = getIntent().getExtras();
        if (b != null) {
            serverAddress = (InetAddress) getIntent().getExtras().getSerializable("serverAddress");
        }

        if (serverAddress == null) {
            InetAddress broadcastAddr =
                Util.getBroadcastAddr(
                    Util.getWifiIpAddress(
                        (WifiManager) getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE)
                    )
                );

            if (broadcastAddr == null) {
                Log.e("LobbyActivity", "Impossible to get the multicast IP.");
                finish();
                return;
            }

            startServer(
                new ClientServerSynchronizer(getGameClient(), playerName, InetAddress.getLoopbackAddress()),
                String.format("Room created by %s", playerName),
                broadcastAddr
            );
        } else {
            getGameClient().connectServer(playerName, serverAddress);
        }

        ImageButton startButton = (ImageButton) findViewById(R.id.startBtn);
        if (serverAddress == null) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        getGameClient().startGame();
                    } catch (IOException e) {
                        showError(e.getLocalizedMessage());
                    }
                }
            });
        } else {
            startButton.setVisibility(View.INVISIBLE);
        }

        adapter.addAll(getGameClient().getPlayerList());
        adapter.notifyDataSetChanged();

        SetEditableRoomTitle();
    }

    @Override
    public void onGameEvent(GameEventType type, final Object data) {
        final GameEventType t = type;
        final Object d = data;
        final Client.GameEventListener self = this;

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
                    startActivity(intent);
                } else if (t == GameEventType.ROOM_NAME_CHANGED) {
                    titleView.setText((String) data);
                } else if (t == GameEventType.ERROR_OCCURED) {
                    Log.d("anamorph", "Error occured");
                    if (d instanceof Exception) {
                        ((Exception) d).printStackTrace();
                        showToast("A network error occured");
                    } else if (d instanceof String) {
                        showToast((String) d);
                    } else {
                        showToast("Unknown error");
                    }
                }
            }
        });
    }

    // Met le titre de la salle en éditable ou en
    // affichage seulement, si l'utilisateur
    // est l'admin de la salle
    private void SetEditableRoomTitle()
    {
        EditText titleEdit = (EditText) findViewById(R.id.editable_roomTitle_txt);
        titleEdit.setText(getGameClient().getRoomName());
        ImageView titleBackground = (ImageView) findViewById(R.id.editable_roomTitle_bg);

        titleView = (TextView) findViewById(R.id.nonEditable_roomTitle);
        titleView.setText(getGameClient().getRoomName());

        titleEdit.setVisibility(isGameHost() ? View.VISIBLE : View.INVISIBLE);
        titleBackground.setVisibility(isGameHost() ? View.VISIBLE : View.INVISIBLE);
        titleView.setVisibility(isGameHost() ? View.INVISIBLE : View.VISIBLE);

        titleEdit.setOnEditorActionListener(
            new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            getGameClient().sendRoomName(v.getText().toString());
                        } catch (IOException e) {
                            showToast("Server doesn't respond");
                        }
                    }
                    return false;
                }
            });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
