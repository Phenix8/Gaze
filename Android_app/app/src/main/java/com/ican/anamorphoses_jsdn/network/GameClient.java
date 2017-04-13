package com.ican.anamorphoses_jsdn.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 12/04/2017.
 */

public class GameClient extends Thread {

    private static String TAG = "GameClient";

    private Socket socketServer;
    private String playerName;
    private BufferedWriter out;
    private BufferedReader in;

    private boolean connected = false;

    private String winner = null;

    private ArrayList<GameEventListener> listeners = new ArrayList<>();

    public interface GameEventListener {
        enum GameEventType {
            PLAYER_LIST_CHANGED,
            GAME_STARTED,
            GAME_ENDED,
            DEATH_MATCH,
            ERROR_OCCURED
        }

        void onGameEvent(GameEventType type, Object data);
    }

    public void connectDistantServer(String playerName, InetAddress serverAddress)
        throws IOException {
        this.playerName = playerName;
        this.socketServer = new Socket(serverAddress, Common.TCP_PORT);
        this.connected = true;
        this.start();
    }

    public void ConnectLocalServer(String playerName)
        throws IOException {
        this.playerName = playerName;
        this.socketServer = new Socket(InetAddress.getLoopbackAddress(), Common.TCP_PORT);
        this.connected = true;
        this.start();
    }

    private void configureStreams(Socket socket)
        throws IOException {
        this.socketServer = socket;
        out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
    }

    private void sendPlayerName()
        throws IOException {
        out.write(String.format("%s\n", playerName));
        out.flush();
    }

    private List<String> parsePlayerList(String data) {
        return Arrays.asList(data.split(":"));
    }

    public void disconnect() {
        boolean threadJoined = false;
        connected = false;
        while (!threadJoined)
        try {
            this.wait();
            threadJoined = true;
        } catch (InterruptedException e) {}
    }

    private void notifyListener(GameEventListener.GameEventType type, Object data) {
        for (GameEventListener listener : listeners) {
            listener.onGameEvent(type, data);
        }
    }

    public void addGameEventListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void removeGameEventListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        try {
            configureStreams(this.socketServer);
            sendPlayerName();

            while (connected) {
                String message = in.readLine();
                Log.d(TAG, String.format("received : %s", message));

                String[] parts = message.split(" ");

                switch (parts[0]) {
                    case "PLAYERS":
                        notifyListener(
                                GameEventListener.GameEventType.PLAYER_LIST_CHANGED,
                                parsePlayerList(parts[1]));
                    break;
                }
            }
        } catch (IOException e) {

        } finally {
            try {
                socketServer.close();
            } catch (IOException e) {}
        }
    }
}
