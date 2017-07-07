package com.ican.anamorphoses_jsdn.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ican.anamorphoses_jsdn.control.Player;
import com.ican.anamorphoses_jsdn.network.Client;
import com.ican.anamorphoses_jsdn.network.ClientServerSynchronizer;

import java.util.ArrayList;

/**
 * Created by Greg on 17/06/2017.
 */

public class GazeActivity extends AppCompatActivity implements Client.GameEventListener {

    protected Client getGameClient() {
        return ((GazeApplication) getApplication()).getGameClient();
    }

    protected void startServer(ClientServerSynchronizer synch, String roomName) {
        ((GazeApplication) getApplication()).startServer(synch, roomName);
    }

    protected void showToast(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }

    protected void showToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    @Override
    public void onGameEvent(Client.GameEventListener.GameEventType type, Object data) {
        switch (type) {
            case GAME_ENDED:
                Intent intent = new Intent(this, LeaderboardActivity.class);
                intent.putExtra("playersList", (ArrayList<Player>)data);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;

            case DEATH_MATCH:
                String anamorphId = (String) data;
                Intent intent1 = new Intent(this, DeathMatchAnnounceActivity.class);
                intent1.putExtra("id", anamorphId);
                intent1.setFlags(intent1.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent1);
                break;

            case ERROR_OCCURED:
                Exception e = (Exception) data;
                e.printStackTrace();
                break;
        }
    }
}
