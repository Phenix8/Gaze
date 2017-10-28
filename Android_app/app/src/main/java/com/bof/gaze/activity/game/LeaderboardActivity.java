package com.bof.gaze.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bof.gaze.R;
import com.bof.gaze.activity.common.CommonGameActivity;
import com.bof.gaze.activity.menu.MenuActivity;
import com.bof.gaze.model.Player;

import java.util.Collection;


public class LeaderboardActivity extends CommonGameActivity {
        //implements Client.GameEventListener, AdapterView.OnItemClickListener {

    private TextView[] tvPlayerNames = new TextView[4];
    private TextView[] tvPlayerScores = new TextView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        tvPlayerNames[0] = (TextView) findViewById(R.id.name_player1);
        tvPlayerNames[1] = (TextView) findViewById(R.id.name_player2);
        tvPlayerNames[2] = (TextView) findViewById(R.id.name_player3);
        tvPlayerNames[3] = (TextView) findViewById(R.id.name_player4);

        tvPlayerScores[0] = (TextView) findViewById(R.id.score_player1);
        tvPlayerScores[1] = (TextView) findViewById(R.id.score_player2);
        tvPlayerScores[2] = (TextView) findViewById(R.id.score_player3);
        tvPlayerScores[3] = (TextView) findViewById(R.id.score_player4);

        Collection<Player> players = getGameClient().getPlayerList();
        loadPlayerScores(players);

        ImageView backButton = (ImageView) findViewById(R.id.leaderboard_act_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }


    private void loadPlayerScores(Collection<Player> players)
    {
        for (TextView tv : tvPlayerNames) {
            tv.setText("");
        }

        for (TextView tv : tvPlayerScores) {
            tv.setText("");
        }

        int i=0;
        for (Player p : players) {
            if (i > 4) {
                break;
            }
            tvPlayerNames[i].setText(p.getName());
            tvPlayerScores[i].setText(String.valueOf(p.getScore()));
            i++;
        }
    }

    @Override
    public void onBackPressed() {
        if (this.isGameHost()) {
            stopServer();
        }
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
