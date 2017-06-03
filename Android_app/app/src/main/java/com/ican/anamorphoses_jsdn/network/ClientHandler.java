package com.ican.anamorphoses_jsdn.network;

import java.net.Socket;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.net.SocketTimeoutException;

public class ClientHandler extends Thread {

    private Socket sock;
    private BufferedWriter out;
    private BufferedReader in;

    private boolean running = true;

    private ArrayList<ClientHandlerListener> listeners = new ArrayList<>();

    public interface ClientHandlerListener {
        void onMessageReceived(ClientHandler handler, String message);
        void onClientDisconnected(ClientHandler handler);
    }

    public ClientHandler(Socket sock)
            throws IOException {
        this.sock = sock;
        this.sock.setSoTimeout(1000);

        out = new BufferedWriter(
                new OutputStreamWriter(
                        this.sock.getOutputStream()
                )
        );

        in = new BufferedReader(
                new InputStreamReader(
                        this.sock.getInputStream()
                )
        );
    }

    public void sendMessage(String message) {
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            this.close();
        }
    }

    public void addListener(ClientHandlerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ClientHandlerListener listener) {
        listeners.remove(listener);
    }

    public void close(){
        this.running = false;
        boolean threadJoined = false;
        while (!threadJoined) {
            try {
                this.join();
                threadJoined = true;
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public void run() {

        try {
            while (running) {
                try {
                    String message = in.readLine();
                    for (ClientHandlerListener listener : listeners) {
                        listener.onMessageReceived(this, message);
                    }
                } catch (SocketTimeoutException e) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                for (ClientHandlerListener listener : listeners) {
                    listener.onClientDisconnected(this);
                }
                listeners.clear();
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return this.sock.getInetAddress().toString();
    }
}
