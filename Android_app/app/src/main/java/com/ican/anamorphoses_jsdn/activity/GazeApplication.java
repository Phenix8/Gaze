package com.ican.anamorphoses_jsdn.activity;

import android.app.Application;

import com.ican.anamorphoses_jsdn.control.Server;
import com.ican.anamorphoses_jsdn.network.Client;
import com.ican.anamorphoses_jsdn.network.ClientServerSynchronizer;
import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.RoomNotifier;
import com.ican.anamorphoses_jsdn.network.ServerBase;
import com.ican.anamorphoses_jsdn.resource.AnamorphDictionary;

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

        server = new Server(new RoomNotifier(Common.BROADCAST_MESSAGE, roomName, Common.UDP_PORT), Common.TCP_PORT, 4);
        server.startListening(synch);
    }

    public AnamorphDictionary getAnamorphDictionary() {
        return anamorphDictionary;
    }
}
