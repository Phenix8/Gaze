package com.bof.gaze.activity.common;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bof.gaze.application.GazeApplication;
import com.bof.gaze.network.Client;
import com.bof.gaze.network.ClientServerSynchronizer;
import com.bof.gaze.model.AnamorphDictionary;

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

    protected void stopServer() {
        ((GazeApplication) getApplication()).stopServer();
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

    protected void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    protected void showInfo(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
