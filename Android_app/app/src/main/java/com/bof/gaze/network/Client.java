package com.bof.gaze.network;

import android.util.Log;

import com.bof.gaze.model.Anamorphosis;
import com.bof.gaze.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Client extends Thread implements Serializable {

    private static String TAG = "Client";

    private ReentrantLock mutex = new ReentrantLock();

    private Socket socketServer;
    private String playerName;
    private BufferedWriter out;
    private BufferedReader in;
    private InetAddress serverAddress;

    private boolean connected = false;

    private GameEventListener listener = null;

    private String playerId = null;
    private int score = 0;
    private int nbFoundAnamorphosis = 0;
    private boolean lobby = true;

    private List<Player> players = new ArrayList<>();
    private String roomName = null;

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

    public void connectServer(String playerName, InetAddress serverAddress) {
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

    private void sendInstruction(final String instruction)
        throws IOException {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    out.write(String.format("%s\n", instruction));
                    out.flush();
                } catch (IOException e) {
                    notifyListener(GameEventListener.GameEventType.ERROR_OCCURED, e);
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private void sendPlayerName()
        throws IOException {
        sendInstruction(
                Protocol.buildConnectInstruction(playerName)
        );
    }

    public void toggleReady()throws IOException {
        sendInstruction(Protocol.buildReadyInstruction(playerId));
    }

    private void annouceAllFound() throws IOException {
        sendInstruction(Protocol.buildFinishedInstruction());
    }

    public void setFound(Anamorphosis a) throws IOException {
        this.score += a.getValue();
        this.nbFoundAnamorphosis++;
        if (nbFoundAnamorphosis >= 4) {
            annouceAllFound();
        }
    }

    public void startGame() throws IOException {
        sendInstruction(Protocol.buildStartInstruction());
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

    public List<Player> getPlayerList() {
        return players;
    }

    public String getRoomName() { return roomName; }

    public int getScore() {
        return this.score;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    private void notifyListener(GameEventListener.GameEventType type, Object data) {
        mutex.lock();
            if (listener != null) {
                listener.onGameEvent(type, data);
            }
        mutex.unlock();
    }

    public void setGameEventListener(GameEventListener listener) {
        mutex.lock();
            this.listener = listener;
        mutex.unlock();
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
                    case Protocol.PLAYERS_INSTRUCTION_TYPE:
                        players = Protocol.parsePlayerListInstructionData(Protocol.parseInstructionData(message));
                            if (lobby) {
                                notifyListener(GameEventListener.GameEventType.PLAYER_LIST_CHANGED, players);
                            } else {
                                notifyListener(GameEventListener.GameEventType.GAME_ENDED, players);
                            }
                    break;

                    case Protocol.PLAYER_ID_INSTRUCTION_TYPE:
                        this.playerId = Protocol.parseInstructionData(message);
                    break;

                    case Protocol.ALREADY_STARTED_INSTRUCTION_TYPE:
                        notifyListener(
                                GameEventListener.GameEventType.ERROR_OCCURED,
                                "Game already stated"
                        );
                    break;

                    case Protocol.START_INSTRUCTION_TYPE:
                        notifyListener(GameEventListener.GameEventType.GAME_STARTED, null);
                        lobby = false;
                    break;

                    case Protocol.NOT_READY_INSTRUCTION_TYPE:
                        notifyListener(
                                GameEventListener.GameEventType.ERROR_OCCURED,
                                "Not all players are ready.");
                        break;

                    case Protocol.DEATHMATCH_INSTRUCTION_TYPE:
                        String id =
                                Protocol.parseDeathMatchInstruction(
                                        Protocol.parseInstructionData(message),
                                        this.playerId
                                );
                        notifyListener(GameEventListener.GameEventType.DEATH_MATCH, id);
                    break;

                    case Protocol.FINISHED_INSTRUCTION_TYPE:
                        sendInstruction(Protocol.buildScoreInstruction(score));
                    break;
                }
            }
        } catch (Exception e) {
            notifyListener(GameEventListener.GameEventType.ERROR_OCCURED, e);
        } finally {
            try {
                if (socketServer != null) {
                    socketServer.close();
                }
            } catch (IOException e) {}
        }
    }
}
