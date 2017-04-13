package com.ican.anamorphoses_jsdn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.RoomFinder;

import java.net.InetAddress;
import java.util.HashMap;

public class JoinGameActivity extends AppCompatActivity implements RoomFinder.RoomListChangeListener {

    RoomFinder finder = new RoomFinder(Common.BROADCAST_MESSAGE, Common.UDP_PORT);
    ListView gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameList = (ListView) findViewById(R.id.gameList);

        finder.addRoomListChangeListener(this);
        finder.startListening();
    }

    @Override
    public void onRoomListChanged(HashMap<InetAddress, String> roomList) {
        gameList.setAdapter(
                new ArrayAdapter<String>(
                        this,
                        R.id.gameList,
                        (String[]) roomList.values().toArray()
                )
        );
    }
}
