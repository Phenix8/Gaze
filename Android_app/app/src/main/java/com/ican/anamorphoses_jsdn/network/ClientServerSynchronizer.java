package com.ican.anamorphoses_jsdn.network;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Created by root on 05/07/2017.
 */

public class ClientServerSynchronizer implements Server.ServerStateCallback {

    private InetAddress adresse;
    private String playerName;
    private Client client;

    public ClientServerSynchronizer(Client client, String playerName, InetAddress adresse) {
        this.client = client;
        this.playerName = playerName;
        this.adresse = adresse;
    }

    @Override
    public void onServerStarted() {
        client.connectServer(playerName, adresse);
    }
}
