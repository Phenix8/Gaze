package com.ican.gaze.activity.common;

import android.content.Intent;

import com.ican.gaze.activity.game.DeathMatchAnnounceActivity;
import com.ican.gaze.activity.game.LeaderboardActivity;
import com.ican.gaze.model.Player;
import com.ican.gaze.network.Client;

import java.util.ArrayList;

/**
 * Created by root on 07/07/2017.
 */

public class CommonGameActivity extends CommonGazeActivity implements Client.GameEventListener {

    @Override
    protected void onResume() {
        super.onResume();

        getGameClient().setGameEventListener(this);
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
