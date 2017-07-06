package com.ican.anamorphoses_jsdn.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ican.anamorphoses_jsdn.network.Client;
import com.ican.anamorphoses_jsdn.network.ClientServerSynchronizer;

/**
 * Created by Greg on 17/06/2017.
 */

public class GazeActivity extends AppCompatActivity {

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
}
