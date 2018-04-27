package com.bof.gaze.activity.common;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bof.gaze.application.GazeApplication;
import com.bof.gaze.network.client.Client;
import com.bof.gaze.network.client.ClientServerSynchronizer;
import com.bof.gaze.model.AnamorphDictionary;

import java.net.InetAddress;

/**
 * Created by Greg on 17/06/2017.
 */

public class CommonGazeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected Client getGameClient() {
        return ((GazeApplication) getApplication()).getGameClient();
    }

    protected void startServer(ClientServerSynchronizer synch, String roomName, InetAddress multicastAddr) {
        ((GazeApplication) getApplication()).startServer(synch, roomName, multicastAddr);
    }

    protected void stopServer() {
        ((GazeApplication) getApplication()).stopServer();
    }

    protected boolean isGameHost() {
        return ((GazeApplication) this.getApplication()).isHost();
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
