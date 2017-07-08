package com.ican.gaze.activity.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ican.gaze.R;
import com.ican.gaze.activity.common.CommonGazeActivity;
import com.ican.gaze.activity.game.AnamorphosisChoiceActivity;
import com.ican.gaze.model.Player;
import com.ican.gaze.network.Client;
import com.ican.gaze.network.ClientServerSynchronizer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends CommonGazeActivity
        implements Client.GameEventListener {

    private ArrayAdapter<Player> adapter;

    public static boolean isRoomAdmin;

    private InetAddress serverAddress = null;

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

        SetEditableRoomTitle();

        ///////////////////////////
        // IMAGE BUTTON "Return" //
        ///////////////////////////

        // Evénement de click sur le boutton "Return"
        ImageButton returnButton = (ImageButton)findViewById(R.id.returnImgButton);
        returnButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
            // retour
            Log.i("Finish", "Finish nickname activity");
            finish();
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
            startServer(
                new ClientServerSynchronizer(getGameClient(), playerName, InetAddress.getLoopbackAddress()),
                String.format("Room created by %s", playerName)
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
                } else if (t == GameEventType.ERROR_OCCURED) {
                    Log.d("anamorph", "Error occured");
                    if (d instanceof Exception) {
                        ((Exception) d).printStackTrace();
                        showError(((Exception) d).getLocalizedMessage());
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
        ImageView titleBackground = (ImageView) findViewById(R.id.editable_roomTitle_bg);

        TextView titleView = (TextView) findViewById(R.id.nonEditable_roomTitle);

        // Assignation du nom de salle depuis le réseau si le joueur n'est pas admin
        if (!isRoomAdmin) {
            titleView.setText("");
        }

        titleEdit.setVisibility(isRoomAdmin ? View.VISIBLE : View.INVISIBLE);
        titleBackground.setVisibility(isRoomAdmin ? View.VISIBLE : View.INVISIBLE);
        titleView.setVisibility(isRoomAdmin ? View.INVISIBLE : View.VISIBLE);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // Vérifie si chaque joueur et prêt et met à jour l'UI
    // le cas échéant
    private void CheckReadyPlayerStates()
    {

    }
}
