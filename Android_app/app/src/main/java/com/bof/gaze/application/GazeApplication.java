package com.bof.gaze.application;

import android.app.Application;

import com.bof.gaze.network.Server;
import com.bof.gaze.network.Client;
import com.bof.gaze.network.ClientServerSynchronizer;
import com.bof.gaze.network.Common;
import com.bof.gaze.network.RoomNotifier;
import com.bof.gaze.network.ServerBase;
import com.bof.gaze.model.AnamorphDictionary;

import java.net.InetAddress;

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

    public void startServer(ClientServerSynchronizer synch, String roomName, InetAddress multicastAddr) {
        if (server != null) {
            return;
        }

        server =
            new Server(
                new RoomNotifier(
                    Common.BROADCAST_MESSAGE,
                    roomName, multicastAddr, Common.UDP_PORT
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
