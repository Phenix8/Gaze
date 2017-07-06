package com.ican.anamorphoses_jsdn.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ican.anamorphoses_jsdn.R;


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
        //

        //}

    }

    /*

    @Override
    public void onGameEvent(GameEventType type, Object data) {

    }
    */
}
