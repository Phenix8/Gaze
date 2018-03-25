package com.bof.gaze.network.server;

import com.bof.gaze.model.Anamorphosis;
import com.bof.gaze.model.Player;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by root on 14/04/2017.
 */

public class Protocol {

    public static final String INSTRUCTION_SEPARATOR = " ";
    public static final String DATA_SEPARATOR = ":";
    public static final String INSTRUCTION_END = "\n";

    /**
     * Server's instructions, sent to clients.
     */
    public static final String PLAYERS_INSTRUCTION_TYPE =           "PLAYERS";
    public static final String PLAYER_ID_INSTRUCTION_TYPE =         "ID";
    public static final String QUIT_INSTRUCTION_TYPE =              "QUIT";
    public static final String CONNECT_INSTRUCTION_TYPE =           "CONNECT";
    public static final String DISCONNECT_INSTRUCTION_TYPE =        "DISCONNECT";
    public static final String START_INSTRUCTION_TYPE =             "START";
    public static final String FINISHED_INSTRUCTION_TYPE =          "FINISHED";
    public static final String NOT_READY_INSTRUCTION_TYPE =         "NOTREADY";
    public static final String ALREADY_STARTED_INSTRUCTION_TYPE =   "GAME-STARTED";
    public static final String DEATHMATCH_INSTRUCTION_TYPE =        "DEATHMATCH";
    public static final String SERVER_STOPPED_INSTRUCTION =         "SERVER-STOPPED";
    public static final String ROOM_NAME_INSTRUCTION_TYPE =         "ROOM-NAME";

    /**
     * Client's messages, sent to server.
     */
    public static final String START_MESSAGE_TYPE =                 "START";
    public static final String QUIT_MESSAGE_TYPE =                  "QUIT";
    public static final String CONNECT_MESSAGE_TYPE =               "CONNECT";
    public static final String DISCONNECT_MESSAGE_TYPE =            "DISCONNECT";
    public static final String FINISHED_MESSAGE_TYPE =              "FINISHED";
    public static final String SCORE_MESSAGE_TYPE =                 "SCORE";
    public static final String READY_MESSAGE_TYPE =                 "READY";
    public static final String ROOM_NAME_MESSAGE_TYPE =             "ROOM-NAME";
    public static final String ANAMORPHOSIS_FOUND_MESSAGE_TYPE =    "ANAM-FOUND";
    public static final String RECONNECT_MESSAGE_TYPE =             "RECONNECT";

    public static final String QUIT_INSTRUCTION =
            QUIT_MESSAGE_TYPE + INSTRUCTION_END;

    public static String parseInstructionType(String instruction) {
        return instruction.split(INSTRUCTION_SEPARATOR)[0];
    }

    public static String parseInstructionData(String instruction) {
        String[] parts = instruction.split(" ");
        if (parts.length > 1) {
            return parts[1];
        }
        return "";
    }

    private static String buildPlayerListInstruction(String instructionType, Collection<Player> players) {
        StringBuffer str = new StringBuffer();
        for (Player player : players) {
            if (str.length() > 0) {
                str.append(DATA_SEPARATOR);
            }
            try {
            str.append(player.getPlayerId())
                    .append(",")
                    .append(URLEncoder.encode(player.getName(), "UTF-8"))
                    .append(",")
                    .append(player.getScore())
                    .append(",")
                    .append(player.isReady())
                    .append(",")
                    .append(player.getNbFoundAnamorphosis());
            } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
        }
        str.insert(0, INSTRUCTION_SEPARATOR);
        str.insert(0, instructionType);
        str.append(INSTRUCTION_END);
        return str.toString();
    }

    public static  String buildPlayersInstruction(Collection<Player> players) {
        return buildPlayerListInstruction(PLAYERS_INSTRUCTION_TYPE, players);
    }

    public static String buildFinishedInstruction(Collection<Player> players) {
        return buildPlayerListInstruction(FINISHED_INSTRUCTION_TYPE, players);
    }

    public static List<Player> parsePlayerListData(String playerListData) {
        ArrayList<Player> players = new ArrayList<>();
        for(String playerInfos : playerListData.split(":")) {
            String[] infos = playerInfos.split(",");
            try {
                players.add(
                        new Player(
                                URLDecoder.decode(infos[1], "UTF-8"),
                                Integer.parseInt(infos[2]),
                                Boolean.parseBoolean(infos[3]),
                                infos[0],
                                Integer.parseInt(infos[4])
                        )
                );
            } catch (UnsupportedEncodingException e) { e.printStackTrace(); }
        }
        return players;
    }

    /**
     * Parse the data part of the death match instruction.
     * @param instructionData The data part of the DEATHMATCH instruction.
     * @param playerId A player's id.
     * @return An anamorphosis's id if player whose id has been submitted is concerned by
     * the death match, null otherwise.
     */
    public static String parseDeathMatchInstruction(String instructionData, String playerId) {
        String[] parts = instructionData.split(DATA_SEPARATOR);

        for (int i=1; i<parts.length; i++) {
            if (parts.equals(playerId)) {
                return parts[1];
            }
        }

        return null;
    }

    public static String buildConnectInstruction(String playerName) {
        String str = null;
        try {
            str = String.format("%s %s%s", CONNECT_MESSAGE_TYPE, URLEncoder.encode(playerName, "UTF-8"), INSTRUCTION_END);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseConnectInstructionData(String data) {
        String str = null;
        try {
            str = URLDecoder.decode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String buildDisconnectInstruction(String playerName) {
        return String.format("%s %s%s", DISCONNECT_MESSAGE_TYPE, playerName, INSTRUCTION_END);
    }

    public static String buildQuitInstruction() {
        return QUIT_INSTRUCTION;
    }

    public static String buildAlreadyStartedInstruction() {
        return ALREADY_STARTED_INSTRUCTION_TYPE;
    }

    public static String buildStartInstruction() {
        return String.format("%s%s", START_INSTRUCTION_TYPE, INSTRUCTION_END);
    }

    public static String buildReadyInstruction(String playerId) {
        return String.format(
                "%s%s%s%s",
                READY_MESSAGE_TYPE,
                INSTRUCTION_SEPARATOR,
                playerId,
                INSTRUCTION_END
        );
    }

    public static String buildNotReadyInstruction() {
        return String.format("%s%s", NOT_READY_INSTRUCTION_TYPE, INSTRUCTION_END);
    }

    public static String buildPlayerIDInstruction(String playerId) {
        return String.format(
                "%s%s%s%s",
                PLAYER_ID_INSTRUCTION_TYPE,
                INSTRUCTION_SEPARATOR,
                playerId,
                INSTRUCTION_END
        );
    }

    public static String buildDeathMatchInstruction(
            List<Player> equalsPlayers, String anamorphId) {

        StringBuffer str = new StringBuffer();
        str.append(DEATHMATCH_INSTRUCTION_TYPE);
        str.append(INSTRUCTION_SEPARATOR);
        str.append(anamorphId);

        for (Player p : equalsPlayers) {
            str.append(DATA_SEPARATOR);
            str.append(p.getPlayerId());
        }

        str.append(INSTRUCTION_END);
        return str.toString();
    }

    public static String buildServerStoppedInstruction() {
        return String.format("%s%s", SERVER_STOPPED_INSTRUCTION, INSTRUCTION_END);
    }

    public static String buildRoomNameInstruction(
            String roomName) {
        try {
            return String.format(
                    "%s%s%s\n",
                    ROOM_NAME_INSTRUCTION_TYPE,
                    INSTRUCTION_SEPARATOR,
                    URLEncoder.encode(roomName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return INSTRUCTION_END;
        }
    }

    public static String parseRoomNameInstruction(String instructionData) {
        try {
            return URLDecoder.decode(instructionData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String buildAnamorphosisFoundMessage(Anamorphosis anam) {
        return String.format("%s %d%s", ANAMORPHOSIS_FOUND_MESSAGE_TYPE, anam.getDifficulty().ordinal(), INSTRUCTION_END);
    }

    public static Anamorphosis.Difficulty parseAnamorphosisFoundMessage(String instructionData)
            throws NumberFormatException{
        return Anamorphosis.Difficulty.values()[Integer.parseInt(instructionData)];
    }

    public static String buildReconnectMessageType(String playerId) {
        return String.format("%s%s%s%s", RECONNECT_MESSAGE_TYPE, INSTRUCTION_SEPARATOR, playerId, INSTRUCTION_END);
    }

    public static String parsePlayerIdInstruction(String instructionData) {
        return instructionData;
    }
}
