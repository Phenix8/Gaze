package com.ican.anamorphoses_jsdn.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by root on 12/04/2017.
 */

public class RoomNotifier extends Thread {

    private String broadcastMessage;

    private int udpPort;

    private boolean notifying;

    private long notifyingInterval = 1000;

    public RoomNotifier(String broadcastMessage, String roomName, int udpPort) {
        this.broadcastMessage = String.format("%s:%s\n", broadcastMessage, roomName);
        this.udpPort = udpPort;
    }

    public void setNotifyingInterval(long millis) {
        this.notifyingInterval = millis;
    }

    public void stopNotifying() {
        boolean threadJoined = false;
        notifying = false;
        while (!threadJoined){
            try {
                this.wait();
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
        try {
            DatagramSocket socket = new DatagramSocket(udpPort);
            socket.setBroadcast(true);
            DatagramPacket packet =
                    new DatagramPacket(
                        broadcastMessage.getBytes(),
                        broadcastMessage.length(),
                        Util.getBroadcast(Util.getIpAddress()),
                        udpPort
                    );

            while (notifying) {
                socket.send(packet);
                try {
                    Thread.sleep(notifyingInterval);
                } catch (InterruptedException e) {}
            }
            socket.close();
        } catch (SocketException e) {

        } catch (IOException e) {

        }
    }

}
