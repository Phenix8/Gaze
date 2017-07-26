package com.ican.gaze.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Created by root on 12/04/2017.
 */

public class RoomNotifier extends Thread implements Serializable {

    private String broadcastMessage;

    private InetAddress broadcastAddr;
    private int udpPort;

    private boolean notifying;

    private long notifyingInterval = 1000;

    public RoomNotifier(String broadcastMessage, String roomName, InetAddress broadcastAddr, int udpPort) {
        try {
            this.broadcastMessage = String.format("%s:%s\n", broadcastMessage, URLEncoder.encode(roomName, "UTF-8"));
            this.broadcastAddr = broadcastAddr;
            this.udpPort = udpPort;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
            DatagramPacket packet =
                    new DatagramPacket(
                        broadcastMessage.getBytes(),
                        broadcastMessage.length(),
                        broadcastAddr,
                        udpPort
                    );

            while (notifying) {
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
