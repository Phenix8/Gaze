package com.ican.anamorphoses_jsdn.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Created by root on 12/04/2017.
 */

public class GameServer extends Thread
    implements ClientHandler.ClientHandlerListener {

    private static String TAG = "GameServer";

    private RoomNotifier roomNotifier;

    private int tcpPort;

    private boolean listening = true;

    private int maxPlayer;

    private HashMap<ClientHandler, String> players = new HashMap<>();

    private int numberOfPlayerReady = 0;

    private GameServer(RoomNotifier roomNotifier, int tcpPort, int maxPlayer) {
        this.tcpPort = tcpPort;
        this.roomNotifier = roomNotifier;
        this.maxPlayer = maxPlayer;
    }

    public static GameServer create(RoomNotifier roomNotifier, int tcpPort, int maxPlayer) {
        return new GameServer(roomNotifier, tcpPort, maxPlayer);
    }

    public void stopListening() {
        this.listening = false;
    }

    public void sendMessageToAllPlayers(String message) {
        for (ClientHandler client : players.keySet()) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                Log.d(TAG, "Error sending to " + players.get(client));
            }
        }
    }

    @Override
    public void run() {
        ServerSocket listeningSocket = null;

        try {
            listeningSocket = new ServerSocket(tcpPort);
            listeningSocket.setSoTimeout(1000);
            roomNotifier.startNotifying();

            while (listening && players.size() < maxPlayer) {
                try {
                    Socket socketClient = listeningSocket.accept();
                    Log.d(TAG, "Client connected (" + socketClient.getInetAddress() + ")");

                    ClientHandler client = new ClientHandler(socketClient);
                    client.addListener(this);
                    client.start();
                } catch (SocketTimeoutException e) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            roomNotifier.stopNotifying();
            try {
                if (listeningSocket != null) {
                    listeningSocket.close();
                }
            } catch (IOException e) {

            }
        }
    }

    @Override
    public void onMessageReceived(ClientHandler client, String message) {
        switch (Protocol.parseInstructionType(message)) {
            case Protocol.CONNECT_INSTRUCTION_TYPE:
                players.put(client, message);
                sendMessageToAllPlayers(
                        Protocol.buildPlayerListInstruction(
                                players.values()
                        )
                );
            break;

            case Protocol.READY_INSTRUCTION_TYPE:

            break;
        }
    }

    @Override
    public void onClientDisconnected(ClientHandler client) {

    }
}
