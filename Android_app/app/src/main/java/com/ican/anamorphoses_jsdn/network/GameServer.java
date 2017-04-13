package com.ican.anamorphoses_jsdn.network;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by root on 12/04/2017.
 */

public class GameServer extends Thread {

    private static String TAG = "GameServer";

    private boolean listening = true;

    @Override
    public void run() {
        //RoomNotifier notifier = new RoomNotifier();
        ServerSocket listeningSocket;

        try {
            listeningSocket = new ServerSocket(Common.TCP_PORT);
            //notifier.start();

            while (listening) {
                Socket socketClient = listeningSocket.accept();
                Log.d(TAG, "Client connected (" + socketClient.getInetAddress() + ")");

                BufferedWriter out =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        socketClient.getOutputStream()));

                out.write("HELLO");
            }

        } catch (IOException e) {

        }
    }

}
