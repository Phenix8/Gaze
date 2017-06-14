package com.ican.anamorphoses_jsdn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.Room;
import com.ican.anamorphoses_jsdn.network.RoomFinder;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class JoinRoomActivity extends AppCompatActivity
        implements RoomFinder.RoomListChangeListener, AdapterView.OnItemClickListener {

    RoomFinder finder = new RoomFinder(Common.BROADCAST_MESSAGE, Common.UDP_PORT);
    ListView gameList;
    ArrayAdapter adapter;
    Button joinButton;
    ArrayList<Room> games = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game_layout);

        gameList = (ListView) findViewById(R.id.gameList);
        adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item,
                games
        );
        gameList.setAdapter(adapter);
        gameList.setOnItemClickListener(this);

        finder.addRoomListChangeListener(this);
        finder.startListening();
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
        startActivity(intent);
        finish();
    }
}
