package com.ican.gaze.activity.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ican.gaze.R;
import com.ican.gaze.model.Player;

import java.util.Collection;


public class LeaderboardActivity extends AppCompatActivity {
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

        Collection<Player> players = (Collection<Player>) getIntent().getSerializableExtra("playersList");
        loadPlayerScores(players);
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
}
