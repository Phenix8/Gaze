package com.ican.gaze.activity.menu;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ican.gaze.R;
import com.ican.gaze.network.Common;
import com.ican.gaze.model.Room;
import com.ican.gaze.network.RoomFinder;
import com.ican.gaze.network.Util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class JoinRoomActivity extends AppCompatActivity
        implements RoomFinder.RoomListChangeListener, AdapterView.OnItemClickListener {

    RoomFinder finder;
    ListView gameList;
    ArrayAdapter adapter;
    Button joinButton;
    ArrayList<Room> games = new ArrayList<>();

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

        gameList = (ListView) findViewById(R.id.gameList);
        adapter = new ArrayAdapter<>(
                this,
                R.layout.join_game_layout_list_item,
                games
        );
        gameList.setAdapter(adapter);
        gameList.setOnItemClickListener(this);

        finder.addRoomListChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LobbyActivity.isRoomAdmin = false;
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("serverAddress", ((Room)adapter.getItem(position)).getAddress());
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
