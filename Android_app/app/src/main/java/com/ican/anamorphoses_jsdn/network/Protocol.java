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

    public static final String PLAYERS_INSTRUCTION_TYPE = "PLAYERS";
    public static final String QUIT_INSTRUCTION_TYPE = "QUIT";
    public static final String CONNECT_INSTRUCTION_TYPE = "CONNECT";
    public static final String START_INSTRUCTION_TYPE = "START";
    public static final String FINISHED_INSTRUCTION_TYPE = "FINISHED";
    public static final String READY_INSTRUCTION_TYPE = "READY";

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

    public static String buildQuitInstruction() {
        return QUIT_INSTRUCTION;
    }
}
