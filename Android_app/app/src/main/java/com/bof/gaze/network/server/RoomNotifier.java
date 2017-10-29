package com.bof.gaze.network.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.Serializable;
import java.net.URLEncoder;

/**
 * Created by root on 12/04/2017.
 */

public class RoomNotifier extends Thread implements Serializable {

    private String broadcast;

    private String message;
    private String roomName;

    private InetAddress broadcastAddr;
    private int udpPort;

    private boolean notifying;

    private long notifyingInterval = 1000;

    public RoomNotifier(String message, String roomName, InetAddress broadcastAddr, int udpPort) {
        this.message = message;
        this.roomName = roomName;

        this.broadcastAddr = broadcastAddr;
        this.udpPort = udpPort;

        updateBroadcast();
    }

    private void updateBroadcast() {
        try {
            this.broadcast = String.format("%s:%s\n", message, URLEncoder.encode(roomName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
        updateBroadcast();
    }

    public void setNotifyingInterval(long millis) {
        this.notifyingInterval = millis;
    }

    public void stopNotifying() {
        boolean threadJoined = false;
        notifying = false;
        while (!threadJoined){
            try {
                this.join();
                threadJoined = true;
            } catch (InterruptedException e) {}
        }
    }

    public void startNotifying() {
        notifying = true;
        this.start();
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(udpPort);
            socket.setBroadcast(true);

            while (notifying) {
            DatagramPacket packet =
                    new DatagramPacket(
                        broadcast.getBytes(),
                        broadcast.length(),
                        broadcastAddr,
                        udpPort
                    );

                socket.send(packet);
                try {
                    Thread.sleep(notifyingInterval);
                } catch (InterruptedException e) {}
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
