package com.bof.gaze.activity.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bof.gaze.R;
import com.bof.gaze.activity.common.CommonGazeActivity;
import com.bof.gaze.model.Room;
import com.bof.gaze.network.Common;
import com.bof.gaze.network.Util;
import com.bof.gaze.network.client.RoomFinder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

public class JoinRoomActivity extends CommonGazeActivity
        implements RoomFinder.RoomListChangeListener, AdapterView.OnItemClickListener {

    private RoomFinder finder;
    private ArrayAdapter adapter;
    private ArrayList<Room> games = new ArrayList<>();
    private TextView stateBar;

    private int numberOfTouchOnStateBar;
    private AlertDialog.Builder ipPromptDialogueBuilder;

    private AlertDialog.Builder createIPPromptDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect IP directly");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        final JoinRoomActivity self = this;

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    InetAddress addr = InetAddress.getByName(input.getText().toString());
                    Intent intent = new Intent(self, LobbyActivity.class);
                    intent.putExtra("serverAddress", addr);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                } catch (UnknownHostException e) {
                    showToast("Cannot find the specified host");
                } catch (Exception e) {
                    showToast("Provided address is invalid.");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game_layout);

        WifiManager wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        InetAddress broadcastAddr =
            Util.getBroadcastAddr(
                Util.getWifiIpAddress(
                    wifiManager
                )
            );

        if (broadcastAddr == null) {
            finish();
            return;
        }

        finder = new RoomFinder(
                Common.BROADCAST_MESSAGE,
                broadcastAddr,
                Common.UDP_PORT
        );

        ListView gameList = findViewById(R.id.gameList);
        adapter = new ArrayAdapter<>(
                this,
                R.layout.join_game_layout_list_item,
                games
        );
        gameList.setAdapter(adapter);
        gameList.setOnItemClickListener(this);

        ipPromptDialogueBuilder = createIPPromptDialogue();

        stateBar = findViewById(R.id.join_act_state_bar);
        stateBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfTouchOnStateBar++;
                if (numberOfTouchOnStateBar >= 10) {
                    ipPromptDialogueBuilder.show();
                }
            }
        });

        finder.addRoomListChangeListener(this);

        ImageView backButton = findViewById(R.id.join_act_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        numberOfTouchOnStateBar = 0;

        if (!finder.isListening()) {
            finder.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (finder.isListening()) {
            finder.stopListening();
        }
    }

    @Override
    public void onRoomListChanged(HashMap<InetAddress, Room> roomList) {
        final Collection<Room> games = new ArrayList<>(roomList.values());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(games);
                adapter.notifyDataSetChanged();
                stateBar.setText(
                    String.format(
                        Locale.ENGLISH,
                        getResources().getString(R.string.joinActStateBarFormat),
                        games.size()
                    )
                );
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("serverAddress", ((Room)adapter.getItem(position)).getAddress());
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
