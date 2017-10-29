package com.bof.gaze.network.server;

import android.util.Log;

import com.bof.gaze.model.Anamorphosis;
import com.bof.gaze.model.AnamorphDictionary;
import com.bof.gaze.model.Player;
import com.bof.gaze.network.client.ClientHandler;

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
        DEATH_MATCH,
        ENDED
    }

    private GameState gameState;

    private AnamorphDictionary anamorphDictionnary;

    private HashMap<ClientHandler, Player> players = new HashMap<>();

    /**
     * Choisi une anamorphose de difficulté moyenne.
     * @return L'identifiant de l'anamorphose sous form de chaine de caractères.
     */
    private String chooseMediumAnamorph() {
        return String.format(
                Locale.ENGLISH,
                "%d",
                anamorphDictionnary.getRandom(Anamorphosis.Difficulty.MEDIUM, false).getId()
        );
    }

    /**
     * Retrouve le Handler associé à un joueur.
     * @param player Le joueur en question.
     * @return L'objet ClientHandler correspondant à ce joueur.
     */
    private ClientHandler getHandler(Player player) {
        for (HashMap.Entry<ClientHandler, Player> entry : players.entrySet()) {
            if (entry.getValue() == player) {
                return entry.getKey();
            }
        }
        return null;
    }

    private ClientHandler getHandler(String playerId) {
        for (HashMap.Entry<ClientHandler, Player> entry : players.entrySet()) {
            if (entry.getValue().getPlayerId().equals(playerId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Donne la liste des joueurs triée par scores si tous les
     * scores des joueurs sont renseignés.
     * @return La liste triée des joueur sous forme d'ArrayList.
     */
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

    /**
     * Tell if all player are ready.
     * @return
     */
    public boolean areAllPlayerReady() {
        for (Player player : players.values()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a new player from his name and add it to the list of players.
     * @param handler The ClientHandler managing the connection.
     * @param name The name of the player.
     * @return The id of the player, or null if the game is already started.
     */
    public String addPlayer(ClientHandler handler, String name) {
        if (gameState != GameState.LOBBY) {
            return null;
        }
        Player newPlayer = new Player(name, 0, false, UUID.randomUUID().toString());
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

    /**
     * Share all players informations with clients.
     */
    private void sendPlayerList() {
        sendMessageToAll(Protocol.buildPlayersInstruction(players.values()));
    }

    /**
     * When the server is stopped for various reason.
     */
    @Override
    protected void onServerStopped() {
        sendMessageToAll(Protocol.buildServerStoppedInstruction());
    }

    /**
     * When a new message is received.
     * @param handler The handler associated to the transmitter of this message.
     * @param message The message received.
     */
    @Override
    public void onMessageReceived(ClientHandler handler, String message) {
        if (message == null) {
            return;
        }

        Log.d("Server", String.format("Received %s", message));

        String instructionType = Protocol.parseInstructionType(message);
        Player player = players.get(handler);

        if (player == null) {
            if (!instructionType.equals(Protocol.CONNECT_INSTRUCTION_TYPE)
                    && !instructionType.equals(Protocol.RECONNECT_MESSAGE_TYPE))
                return;
        }

        switch (instructionType) {
            case Protocol.CONNECT_MESSAGE_TYPE:
                String name = Protocol.parseConnectInstructionData(Protocol.parseInstructionData(message));
                String playerId = addPlayer(handler, name);
                if (playerId == null) {
                    handler.sendMessage(Protocol.buildAlreadyStartedInstruction());
                } else {
                    handler.sendMessage(Protocol.buildPlayerIDInstruction(playerId));
                    sendPlayerList();
                }
            break;

            case Protocol.RECONNECT_MESSAGE_TYPE:
                String id = Protocol.parsePlayerIdInstruction(
                        Protocol.parseInstructionData(message)
                );
                ClientHandler h = getHandler(id);
                if (h != null) {
                    Player p = players.get(h);
                    players.remove(h);
                    players.put(handler, p);
                    handler.sendMessage(Protocol.buildPlayerIDInstruction(id));
                } else {
                    handler.sendMessage(Protocol.ALREADY_STARTED_INSTRUCTION_TYPE);
                }
            break;

            //A player change it state ready/not ready
            case Protocol.READY_MESSAGE_TYPE:
                player.setReady(!player.isReady());
                sendPlayerList();
            break;

            //The host of the game ask to start the game.
            //Protocol.NOTREADY instruction is returned if not all players are ready.
            case Protocol.START_MESSAGE_TYPE:
                if (areAllPlayerReady()) {
                    this.gameState = GameState.MAIN_GAME;
                    sendMessageToAll(Protocol.buildStartInstruction());
                } else {
                    handler.sendMessage(Protocol.buildNotReadyInstruction());
                }
            break;

            case Protocol.ROOM_NAME_MESSAGE_TYPE:
                //Only host can change room name
                if (handler.isHost()) {
                    roomNotifier.setRoomName(
                            Protocol.parseRoomNameInstruction(
                                    Protocol.parseInstructionData(message)
                            )
                    );
                    sendMessageToAll(message);
                }
            break;

            // When a player found an anmorphosis (update of score + the number of found anamorphosis)
            case Protocol.ANAMORPHOSIS_FOUND_MESSAGE_TYPE:
                player.setNbFoundAnamorphosis((player.getNbFoundAnamorphosis() + 1));
                Anamorphosis.Difficulty anamDifficulty = Protocol.parseAnamorphosisFoundMessage(Protocol.parseInstructionData(message));
                player.setScore(player.getScore() + Anamorphosis.getValueFromDifficulty(anamDifficulty));

                if (gameState == GameState.DEATH_MATCH) {
                    sendMessageToAll(
                            Protocol.buildFinishedInstruction(sortPlayersByScore())
                    );
                    gameState = GameState.ENDED;
                } else if (gameState == GameState.MAIN_GAME) {
                    //If the player have found 4 anamorphosis the game is stopped
                    if (player.getNbFoundAnamorphosis() == 4) {
                        //Check if any players are equals
                        ArrayList<Player> players = sortPlayersByScore();
                        ArrayList<Player> equalsPlayers = new ArrayList<>();
                        equalsPlayers.add(players.get(0));
                        for (int i=1; i<players.size(); i++) {
                            if (players.get(i).getScore() != equalsPlayers.get(i-1).getScore()) {
                                break;
                            }
                            equalsPlayers.add(players.get(i));
                        }
                        //Then DEATH MATCH instruction is sent.
                        if (equalsPlayers.size() > 1) {
                            gameState = GameState.DEATH_MATCH;
                            sendMessageToAll(
                                Protocol.buildDeathMatchInstruction(
                                    equalsPlayers,
                                    chooseMediumAnamorph()
                                )
                            );
                        } else { //Otherwise, we send the final players list
                           sendMessageToAll(
                                   Protocol.buildFinishedInstruction(players)
                           );
                            gameState = GameState.ENDED;
                        }
                      } else {
                        sendPlayerList();
                    }
                }
                break;
        }
    }

    /**
     * When a client is disconected for various reason.
     * @param handler The Handler in charge of the connection
     */
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

    /**
     * Build a new server.
     * @param roomNotifier The UDP room broadcaster.
     * @param anamorphDictionary The register of anamorphosis.
     * @param tcpPort The tcp port on wich the server will wait for new connections.
     * @param maxPlayer The maximum amount of player.
     */
    public Server(RoomNotifier roomNotifier, AnamorphDictionary anamorphDictionary, int tcpPort, int maxPlayer) {
        super(roomNotifier, tcpPort, maxPlayer);
        this.gameState = GameState.LOBBY;
        this.anamorphDictionnary = anamorphDictionary;
    }
}
