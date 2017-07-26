package com.ican.gaze.activity.common;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ican.gaze.application.GazeApplication;
import com.ican.gaze.network.Client;
import com.ican.gaze.network.ClientServerSynchronizer;
import com.ican.gaze.model.AnamorphDictionary;

import java.net.InetAddress;

/**
 * Created by Greg on 17/06/2017.
 */

public class CommonGazeActivity extends AppCompatActivity {

    protected Client getGameClient() {
        return ((GazeApplication) getApplication()).getGameClient();
    }

    protected void startServer(ClientServerSynchronizer synch, String roomName, InetAddress multicastAddr) {
        ((GazeApplication) getApplication()).startServer(synch, roomName, multicastAddr);
    }

    protected void showToast(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }

    protected void showToast(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    protected AnamorphDictionary getAnamorphDictionnary() {
        return ((GazeApplication) getApplication()).getAnamorphDictionary();
    }
}
