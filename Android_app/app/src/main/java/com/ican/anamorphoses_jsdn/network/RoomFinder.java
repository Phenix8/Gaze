package com.ican.anamorphoses_jsdn.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 13/04/2017.
 */

public class RoomFinder extends Thread {

    private String broadcastMessage;
    private int udpPort;
    private boolean listening;

    private HashMap<InetAddress, Game> rooms = new HashMap<>();

    private ArrayList<RoomListChangeListener> listeners = new ArrayList<>();

    public interface RoomListChangeListener {
        void onRoomListChanged(HashMap<InetAddress, Game> roomList);
    }

    public RoomFinder(String broadcastMessage, int udpPort) {
        this.broadcastMessage = broadcastMessage;
        this.udpPort = udpPort;
    }

    private void notifyListeners() {
        for (RoomListChangeListener listener : listeners) {
            listener.onRoomListChanged(rooms);
        }
    }

    public void addRoomListChangeListener(RoomListChangeListener listener) {
        listeners.add(listener);
    }

    public void removeRoomListChangeListener(RoomListChangeListener listener) {
        listeners.remove(listener);
    }

    public void startListening() {
        listening = true;
        this.start();
    }

    public void stopListening() {
        boolean threadJoined = false;
        listening = false;
        while (!threadJoined){
            try {
                this.join();
                threadJoined = true;
            } catch (InterruptedException e) {}
        }
    }

    public boolean isListening() {
        return listening;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            byte[] buffer = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket = new DatagramSocket(udpPort);
            socket.setSoTimeout(100);

            while (listening) {
                try {
                    socket.receive(packet);
                    String[] messages = new String(buffer, 0, packet.getLength(), "UTF-8").split(":");
                    if (messages.length == 2) {
                        if (messages[0].equals(broadcastMessage)) {
                            rooms.put(packet.getAddress(), new Game(packet.getAddress(), messages[1]));
                            notifyListeners();
                        }
                    }
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (SocketException e) {

        } catch (IOException e) {

        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
