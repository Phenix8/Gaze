package com.ican.anamorphoses_jsdn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ican.anamorphoses_jsdn.control.Player;
import com.ican.anamorphoses_jsdn.network.Client;


public class LeaderboardActivity extends AppCompatActivity {
        //implements Client.GameEventListener, AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        LoadPlayerScores();
    }


    private void LoadPlayerScores()
    {
        //for (Player p : tempPlayersList)
        // {
            for (int[] id : new int[][]{{R.id.name_player1, R.id.score_player1},
                    {R.id.name_player2, R.id.score_player2},
                    {R.id.name_player3, R.id.score_player3},
                    {R.id.name_player4, R.id.score_player4}})
            {


            }

        //}

    }

    /*

    @Override
    public void onGameEvent(GameEventType type, Object data) {

    }
    */
}
