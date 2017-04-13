package com.ican.anamorphoses_jsdn.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Created by root on 12/04/2017.
 */

public class GameServer extends Thread {

    private static String TAG = "GameServer";

    private RoomNotifier roomNotifier;

    private int tcpPort;

    private boolean listening = true;

    private int maxPlayer;

    private HashMap<String, Socket> players = new HashMap<>();

    public GameServer(RoomNotifier roomNotifier, int tcpPort, int maxPlayer) {
        this.tcpPort = tcpPort;
        this.roomNotifier = roomNotifier;
        this.maxPlayer = maxPlayer;
    }

    public void stopListening() {
        this.listening = false;
    }

    private void sendPlayerList() {
        StringBuffer str = new StringBuffer();

        for (String player : players.keySet()) {
            if (str.length() > 0) {
                str.append(":");
            }
            str.append(player);
        }
        str.append("\n");
        str.insert(0, "PLAYERS ");
        sendMessageToAllPlayers(str.toString());
    }

    public void sendMessageToAllPlayers(String message) {
        for (Socket sockClient : players.values()) {
            try {
                OutputStreamWriter out =
                        new OutputStreamWriter(
                                sockClient.getOutputStream());

                out.write(message);
                out.flush();

            } catch (IOException e) {
                Log.d(TAG, "Error sending to " + sockClient.getInetAddress());
            }
        }
    }

    @Override
    public void run() {
        ServerSocket listeningSocket;

        try {
            listeningSocket = new ServerSocket(tcpPort);
            listeningSocket.setSoTimeout(1000);
            roomNotifier.startNotifying();

            while (listening && players.size() < maxPlayer) {
                try {
                    Socket socketClient = listeningSocket.accept();
                    Log.d(TAG, "Client connected (" + socketClient.getInetAddress() + ")");

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            socketClient.getInputStream()));

                    String playerName = reader.readLine();
                    players.put(playerName, socketClient);
                    sendPlayerList();
                } catch (SocketTimeoutException e) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            roomNotifier.stopNotifying();
            for (Socket socket : players.values()) {
                try {
                    socket.close();
                } catch (IOException e){}
            }
        }
    }

}
