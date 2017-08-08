package com.bof.gaze.network;

import com.bof.gaze.model.Anamorphosis;
import com.bof.gaze.model.AnamorphDictionary;
import com.bof.gaze.model.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by root on 02/05/2017.
 */

public class Server extends ServerBase {

    private enum GameState {
        LOBBY,
        MAIN_GAME,
        DEATH_MATCH
    }

    private GameState gameState;

    private AnamorphDictionary anamorphDictionnary;

    private HashMap<ClientHandler, Player> players = new HashMap<>();

    private String chooseMediumAnamorph() {
        return String.format(
                Locale.ENGLISH,
                "%d",
                anamorphDictionnary.getRandom(Anamorphosis.Difficulty.MEDIUM, false).getId()
        );
    }

    private ClientHandler getHandler(Player player) {
        for (HashMap.Entry<ClientHandler, Player> entry : players.entrySet()) {
            if (entry.getValue() == player) {
                return entry.getKey();
            }
        }
        return null;
    }

    private ArrayList<Player> sortPlayersByScore() {
        for (Player player : players.values()) {
            if (player.getScore() == -1) {
                return null;
            }
        }
        ArrayList<Player> sortedPlayers = new ArrayList<>(players.values());
        Collections.sort(sortedPlayers, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return p2.getScore() - p2.getScore();
            }
        });
        return sortedPlayers;
    }

    public boolean areAllPlayerReady() {
        for (Player player : players.values()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    public String addPlayer(ClientHandler handler, String name) {
        if (gameState != GameState.LOBBY) {
            return null;
        }
        Player newPlayer = new Player(name, -1, false, UUID.randomUUID().toString());
        players.put(handler, newPlayer);
        return newPlayer.getPlayerId();
    }

    public void startGame() {
        if (players.size() < 2) {
            return;
        }
        for (Player player : players.values()) {
            if (!player.isReady()) {
                return;
            }
        }
    }

    private void sendPlayerList() {
        sendMessageToAll(Protocol.buildPlayerListInstruction(players.values()));
    }

    @Override
    public void onMessageReceived(ClientHandler handler, String message) {
        if (message == null) {
            return;
        }

        switch (Protocol.parseInstructionType(message)) {
            case Protocol.CONNECT_INSTRUCTION_TYPE:
                String name = Protocol.parseConnectInstructionData(Protocol.parseInstructionData(message));
                String playerId = addPlayer(handler, name);
                if (playerId == null) {
                    handler.sendMessage(Protocol.buildAlreadyStartedInstruction());
                } else {
                    handler.sendMessage(Protocol.buildPlayerIDInstruction(playerId));
                    sendPlayerList();
                }
            break;

            case Protocol.READY_INSTRUCTION_TYPE:
                Player player = players.get(handler);
                player.setReady(!player.isReady());
                sendPlayerList();
            break;

            case Protocol.START_INSTRUCTION_TYPE:
                if (areAllPlayerReady()) {
                    this.gameState = GameState.MAIN_GAME;
                    sendMessageToAll(Protocol.buildStartInstruction());
                } else {
                    handler.sendMessage(Protocol.buildNotReadyInstruction());
                }
            break;

            case Protocol.FINISHED_INSTRUCTION_TYPE:
                if (gameState == GameState.DEATH_MATCH) {
                    Player p = players.get(handler);
                    p.setScore(p.getScore()+1);
                    sendMessageToAll(
                            Protocol.buildPlayerListInstruction(
                                    players.values()));
                } else {
                    for (Player p : players.values()) {
                        p.setScore(-1);
                    }
                    sendMessageToAll(Protocol.buildFinishedInstruction());
                }
            break;

            case Protocol.SCORE_INSTRUCTION_TYPE:
                try {
                    int score = Integer.parseInt(Protocol.parseInstructionData(message));
                    players.get(handler).setScore(score);
                    ArrayList<Player> sortedPlayer = sortPlayersByScore();
                    if (sortedPlayer == null) {
                        return;
                    }
                    if (sortedPlayer.get(0) == sortedPlayer.get(1)) {
                        gameState = GameState.DEATH_MATCH;
                        sendMessageToAll(
                                Protocol.buildDeathMatchInstruction(
                                        sortedPlayer.get(0).getPlayerId(),
                                        sortedPlayer.get(1).getPlayerId(),
                                        chooseMediumAnamorph()
                                )
                        );
                    } else {
                        sendMessageToAll(
                            Protocol.buildPlayerListInstruction(
                                sortedPlayer
                            )
                        );
                    }
                } catch (NumberFormatException e) {

                }
            break;
        }
    }

    @Override
    public void onClientDisconnected(ClientHandler handler) {
        if (players.containsKey(handler)) {
            Player player = players.remove(handler);
            if (gameState == GameState.LOBBY) {
                sendPlayerList();
            } else {
                sendMessageToAll(Protocol.buildDisconnectInstruction(player.getName()));
            }
        }
    }

    public Server(RoomNotifier roomNotifier, AnamorphDictionary anamorphDictionary, int tcpPort, int maxPlayer) {
        super(roomNotifier, tcpPort, maxPlayer);
        this.gameState = GameState.LOBBY;
        this.anamorphDictionnary = anamorphDictionary;
    }
}
