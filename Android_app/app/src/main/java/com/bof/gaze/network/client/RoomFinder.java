package com.bof.gaze.network.client;

import com.bof.gaze.model.Room;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 13/04/2017.
 */

public class RoomFinder implements Runnable {

    private String broadcastMessage;
    private InetAddress broadcastAddr;
    private int udpPort;
    private boolean listening;

    private Thread thread;

    private HashMap<InetAddress, Room> rooms = new HashMap<>();

    private ArrayList<RoomListChangeListener> listeners = new ArrayList<>();

    public interface RoomListChangeListener {
        void onRoomListChanged(HashMap<InetAddress, Room> roomList);
    }

    public RoomFinder(String broadcastMessage, InetAddress broadcastAddr, int udpPort) {
        this.broadcastMessage = broadcastMessage;
        this.broadcastAddr = broadcastAddr;
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
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stopListening() {
        listening = false;
        while (this.thread != null){
            try {
                this.thread.join();
                this.thread = null;
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
            socket.setSoTimeout(1000);

            while (listening) {
                try {
                    socket.receive(packet);
                    String[] messages = new String(buffer, 0, packet.getLength(), "UTF-8").split(":");
                    if (messages.length == 2) {
                        if (messages[0].equals(broadcastMessage)) {
                            rooms.put(packet.getAddress(), new Room(packet.getAddress(), URLDecoder.decode(messages[1], "UTF-8")));
                            notifyListeners();
                        }
                    }
                } catch (SocketTimeoutException e) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
