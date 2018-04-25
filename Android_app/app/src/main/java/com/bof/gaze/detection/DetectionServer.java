package com.bof.gaze.detection;

import android.graphics.ImageFormat;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
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

    public int sendDetectionRequest(String detectorName, Image image) throws Exception {
        if (serverAddress == null) {
            throw new Exception("Detection server was not advertised");
        }

        Socket sock = null;

        try {
            sock = new Socket(serverAddress, serverPort);
            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
            DataOutputStream writer = new DataOutputStream(out);
            WritableByteChannel byteWriter = Channels.newChannel(out);

            writer.writeInt(detectorName.length());
            writer.flush();
            writer.write(detectorName.getBytes("US-ASCII"));

            switch (image.getFormat()) {
                case ImageFormat.JPEG:
                    writer.write("jpg".getBytes("US-ASCII"));

                    writer.writeInt(image.getPlanes()[0].getBuffer().remaining());
                    byteWriter.write(image.getPlanes()[0].getBuffer());
                break;

                case ImageFormat.YUV_420_888:
                    writer.write("yuv".getBytes("US-ASCII"));

                    writer.writeInt(image.getWidth());
                    writer.writeInt(image.getHeight());

                    writer.writeInt(image.getPlanes()[0].getRowStride());
                    writer.writeInt(image.getPlanes()[1].getPixelStride());
                    writer.writeInt(image.getPlanes()[1].getRowStride());

                    writer.writeInt(image.getPlanes()[0].getBuffer().remaining());
                    byteWriter.write(image.getPlanes()[0].getBuffer());

                    writer.writeInt(image.getPlanes()[1].getBuffer().remaining());
                    byteWriter.write(image.getPlanes()[1].getBuffer());
                    byteWriter.write(image.getPlanes()[2].getBuffer());
                break;

                default:
                    throw new Exception("Unsupported image format");
            }

            out.flush();

            DataInputStream in = new DataInputStream(sock.getInputStream());

            int result = in.readInt();

            Log.d("DetectionServer", String.format("Server returned %d", result));

            return result;

        } catch (IOException e) {
            throw new Exception("Network error", e);
        } finally {
            try {
                if (sock != null) sock.close();
            } catch (IOException e) {}
        }
    }

    @Override
    protected void finalize() throws Throwable {
        findServerTask.stop();
        super.finalize();
    }
}
