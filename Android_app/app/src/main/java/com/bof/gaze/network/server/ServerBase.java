package com.bof.gaze.network.server;

import android.util.Log;

import com.bof.gaze.network.client.ClientHandler;

import java.io.Serializable;
import java.util.ArrayList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by root on 12/04/2017.
 */

public abstract class
ServerBase implements Runnable, ClientHandler.ClientHandlerListener {

    public interface ServerStateCallback extends Serializable {
        void onServerStarted();
    }

    private ServerStateCallback callback = null;

    private static String TAG = "ServerBase";

    protected RoomNotifier roomNotifier;

    private int tcpPort;

    private boolean listening;

    private Thread thread;

    private int maxPlayer;

    private ArrayList<ClientHandler> clients = new ArrayList<>();

    public ServerBase(RoomNotifier roomNotifier, int tcpPort, int maxPlayer) {
        this.roomNotifier = roomNotifier;
        this.tcpPort = tcpPort;
        this.maxPlayer = maxPlayer;
    }

    synchronized public void startListening(ServerStateCallback callback) {
        if (this.listening) {
            throw new IllegalStateException("Server is already started.");
        }
        this.callback = callback;
        this.thread = new Thread(this);
        this.thread.start();
        this.listening = true;
    }

    synchronized public void stopListening() {
        if (!this.listening) {
            throw new IllegalStateException("Server is not running.");
        }
        this.listening = false;
        while (this.thread != null) {
            try {
                this.thread.join();
                this.thread = null;
            } catch (InterruptedException e) {}
        }
    }

    public void sendMessageToAll(String message) {
        for (ClientHandler client : this.clients) {
            client.sendMessage(message);
        }
    }

    protected abstract void onServerStopped();

    @Override
    public void run() {
        ServerSocket listeningSocket = null;
        try {
            listeningSocket = new ServerSocket(tcpPort);
            listeningSocket.setSoTimeout(1000);
            roomNotifier.startNotifying();

            while (listening) {
                try {
                    if (callback != null) {
                        callback.onServerStarted();
                        callback = null;
                    }
                    Socket socketClient = listeningSocket.accept();
                    Log.d(TAG, "Client connected (" + socketClient.getInetAddress() + ")");

                    ClientHandler client = new ClientHandler(socketClient);
                    client.addListener(this);
                    this.clients.add(client);
                    client.start();
                } catch (SocketTimeoutException e) {

                }
            }

            onServerStopped();
            for (ClientHandler client : clients) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            roomNotifier.stopNotifying();
            try {
                if (listeningSocket != null) {
                    listeningSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
