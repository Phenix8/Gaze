package com.ican.gaze.application;

import android.app.Application;

import com.ican.gaze.network.Server;
import com.ican.gaze.network.Client;
import com.ican.gaze.network.ClientServerSynchronizer;
import com.ican.gaze.network.Common;
import com.ican.gaze.network.RoomNotifier;
import com.ican.gaze.network.ServerBase;
import com.ican.gaze.model.AnamorphDictionary;

/**
 * Created by Greg on 17/06/2017.
 */

public class GazeApplication extends Application {

    private Client gameClient = new Client();
    private ServerBase server = null;
    private AnamorphDictionary anamorphDictionary = new AnamorphDictionary();

    public Client getGameClient() {
        return gameClient;
    }

    public void startServer(ClientServerSynchronizer synch, String roomName) {
        if (server != null) {
            return;
        }

        server =
            new Server(
                new RoomNotifier(
                    Common.BROADCAST_MESSAGE,
                    roomName, Common.UDP_PORT
                ),
                anamorphDictionary,
                Common.TCP_PORT,
                4
            );

        server.startListening(synch);
    }

    public AnamorphDictionary getAnamorphDictionary() {
        return anamorphDictionary;
    }
}
