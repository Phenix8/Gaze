package com.bof.gaze.detection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Locale;

public class DetectionServer {

    private class FindServerTask extends AsyncTask<Object, Void, Void> {
        private boolean listening = true;

        public void stop() {
            listening = false;
        }

        @Override
        protected Void doInBackground(Object... args) {
            String broadcastSignature = (String) args[0];
            int udpPort = (Integer) args[1];

            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            DatagramSocket socket = null;

            while (socket == null) {
                try {
                    socket = new DatagramSocket(udpPort);
                    socket.setSoTimeout(1000);
                    socket.setBroadcast(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            while (listening) {
                try {
                    socket.receive(packet);
                    String message = new String(buffer, 0, packet.getLength(), "UTF-8");
                    String[] messages = message.split(":");

                    if (messages.length == 2) {
                        if (messages[0].equals(broadcastSignature)) {
                            try {
                                serverPort = Integer.parseInt(messages[1]);
                                serverAddress = packet.getAddress();

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (SocketTimeoutException e) {
                } catch (IOException e) {

                }
            }
            socket.close();
            return null;
        }
    }

    private InetAddress serverAddress = null;
    private int serverPort;
    private FindServerTask findServerTask;

    public DetectionServer() {
        findServerTask = new FindServerTask();
        findServerTask.execute("DETECTION SERVER HERE", 5151);
    }

    public boolean isDetectionServerAvailable() {
        return serverAddress != null;
    }

    @Override
    protected void finalize() throws Throwable {
        findServerTask.stop();
        super.finalize();
    }
}
