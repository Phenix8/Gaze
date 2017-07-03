package com.ican.anamorphoses_jsdn;

import android.app.Application;

import com.ican.anamorphoses_jsdn.network.Client;

/**
 * Created by Greg on 17/06/2017.
 */

public class GazeApplication extends Application {

    Client gameClient = new Client();

    public Client getGameClient() {
        return gameClient;
    }
}
