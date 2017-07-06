package com.ican.anamorphoses_jsdn.activity;

import android.app.Application;

import com.ican.anamorphoses_jsdn.control.Manager;
import com.ican.anamorphoses_jsdn.network.Client;
import com.ican.anamorphoses_jsdn.network.ClientServerSynchronizer;
import com.ican.anamorphoses_jsdn.network.Common;
import com.ican.anamorphoses_jsdn.network.RoomNotifier;
import com.ican.anamorphoses_jsdn.network.Server;

/**
 * Created by Greg on 17/06/2017.
 */

public class GazeApplication extends Application {

    private Client gameClient = new Client();
    private Server server = null;

    public Client getGameClient() {
        return gameClient;
    }

    public void startServer(ClientServerSynchronizer synch, String roomName) {
        if (server != null) {
            return;
        }

        server = new Manager(new RoomNotifier(Common.BROADCAST_MESSAGE, roomName, Common.UDP_PORT), Common.TCP_PORT, 4);
        server.startListening(synch);
    }
}
