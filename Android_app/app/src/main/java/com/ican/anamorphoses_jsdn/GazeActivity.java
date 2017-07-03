package com.ican.anamorphoses_jsdn;

import android.support.v7.app.AppCompatActivity;

import com.ican.anamorphoses_jsdn.network.Client;

/**
 * Created by Greg on 17/06/2017.
 */

public class GazeActivity extends AppCompatActivity {

    public Client getGameClient() {
        return ((GazeApplication) getApplication()).getGameClient()
    }

}
