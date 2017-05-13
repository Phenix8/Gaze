package com.ican.anamorphoses_jsdn.network;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by root on 14/04/2017.
 */

public class Protocol {

    public static final String INSTRUCTION_SEPARATOR = " ";
    public static final String DATA_SEPARATOR = ":";
    public static final String INSTRUCTION_END = "\n";

    public static final String PLAYERS_INSTRUCTION_TYPE =           "PLAYERS";
    public static final String QUIT_INSTRUCTION_TYPE =              "QUIT";
    public static final String CONNECT_INSTRUCTION_TYPE =           "CONNECT";
    public static final String DISCONNECT_INSTRUCTION_TYPE =        "DISCONNECT";
    public static final String START_INSTRUCTION_TYPE =             "START";
    public static final String FINISHED_INSTRUCTION_TYPE =          "FINISHED";
    public static final String SCORE_INSTRUCTION_TYPE =             "SCORE";
    public static final String READY_INSTRUCTION_TYPE =             "READY";
    public static final String ALREADY_STARTED_INSTRUCTION_TYPE =   "GAME-STARTED";
    public static final String DEATHMATCH_INSTRUCTION_TYPE =        "DEATHMATCH";

    public static final String QUIT_INSTRUCTION =
            QUIT_INSTRUCTION_TYPE + INSTRUCTION_END;

    public static String parseInstructionType(String instruction) {
        return instruction.split(INSTRUCTION_SEPARATOR)[0];
    }

    public static String parseInstructionData(String instruction) {
        String[] parts = instruction.split(" ");
        if (parts.length > 0) {
            return parts[1];
        }
        return "";
    }

    public static String buildPlayerListInstruction(Collection<String> playerNames) {
        StringBuffer str = new StringBuffer();
        for (String player : playerNames) {
            if (str.length() > 0) {
                str.append(DATA_SEPARATOR);
            }
            str.append(player);
        }
        str.insert(0, INSTRUCTION_SEPARATOR);
        str.insert(0, PLAYERS_INSTRUCTION_TYPE);
        str.append(INSTRUCTION_END);
        return str.toString();
    }

    public static List<String> parsePlayerListInstructionData(String playerListData) {
        return Arrays.asList(playerListData.split(":"));
    }

    public static String buildConnectInstruction(String playerName) {
        return String.format("%s %s%s", CONNECT_INSTRUCTION_TYPE, playerName, INSTRUCTION_END);
    }

    public static String buildDisconnectInstruction(String playerName) {
        return String.format("%s %s%s", DISCONNECT_INSTRUCTION_TYPE, playerName, INSTRUCTION_END);
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

    public static String buildFinishedInstruction() {
        return String.format("%s%s", FINISHED_INSTRUCTION_TYPE, INSTRUCTION_END);
    }

    public static String buildScoreInstruction(int score) {
        StringBuffer str = new StringBuffer(SCORE_INSTRUCTION_TYPE);
        str.append(DATA_SEPARATOR).append(score).append(INSTRUCTION_END);
        return str.toString();
    }

    public static String buildDeathMatchInstruction(
            String playerId1, String playerId2, String anamorphId) {
        return String.format(
                "%s%s%s%s%s%s%s%s",
                DEATHMATCH_INSTRUCTION_TYPE,
                INSTRUCTION_SEPARATOR,
                playerId1,
                DATA_SEPARATOR,
                playerId2,
                DATA_SEPARATOR,
                anamorphId,
                INSTRUCTION_END);
    }
}
