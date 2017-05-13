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

public class Client extends Thread {

    private static String TAG = "Client";

    private Socket socketServer;
    private String playerName;
    private BufferedWriter out;
    private BufferedReader in;
    private InetAddress serverAddress;

    private boolean connected = false;

    private String winner = null;

    private ArrayList<GameEventListener> listeners = new ArrayList<>();

    public interface GameEventListener {
        enum GameEventType {
            PLAYER_LIST_CHANGED,
            PLAYER_STATE_CHANGED,
            GAME_STARTED,
            GAME_ENDED,
            DEATH_MATCH,
            ERROR_OCCURED
        }

        void onGameEvent(GameEventType type, Object data);
    }

    public void connectServer(String playerName, InetAddress serverAddress)
        throws IOException {
        this.playerName = playerName;
        this.serverAddress = serverAddress;
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

    private void sendInstruction(String instruction)
        throws IOException {
        out.write(String.format("%s\n", instruction));
        out.flush();
    }

    private void sendPlayerName()
        throws IOException {
        sendInstruction(
                Protocol.buildConnectInstruction(playerName)
        );
    }

    public void disconnect() {
        boolean threadJoined = false;
        try {
            sendInstruction(Protocol.buildQuitInstruction());
        } catch (IOException e) {};
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
            this.socketServer = new Socket(this.serverAddress, Common.TCP_PORT);
            configureStreams(this.socketServer);
            sendPlayerName();

            while (connected) {
                String message = in.readLine();
                Log.d(TAG, String.format("received : %s", message));

                String instructionType = Protocol.parseInstructionType(message);

                switch (instructionType) {
                    case "PLAYERS":
                        notifyListener(
                                GameEventListener.GameEventType.PLAYER_LIST_CHANGED,
                                Protocol.parseInstructionData(message));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socketServer != null) {
                    socketServer.close();
                }
            } catch (IOException e) {}
        }
    }
}
