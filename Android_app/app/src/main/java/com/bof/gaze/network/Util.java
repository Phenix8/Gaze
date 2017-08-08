package com.bof.gaze.network;

import android.net.wifi.WifiManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * Created by root on 12/04/2017.
 */

public class Util {

    private static String TAG = "network.Util";

    private static final String IPV6_MCAST_ADDR = "FF02::1";
    private static final String IPV4_MCAST_ADDR = "224.0.0.1";

    public static boolean isWifiEnabled(WifiManager manager) {
        return manager.isWifiEnabled();
    }

    public static InetAddress getWifiIpAddress(WifiManager wifiManager) {
        int ipAddr = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddr = Integer.reverseBytes(ipAddr);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddr).toByteArray();

        InetAddress ipAddress;
        try {
            ipAddress = InetAddress.getByAddress(ipByteArray);
            Log.d("Network.Util", ipAddress.toString());
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddress = null;
        }

        return ipAddress;
    }

    public static InetAddress getMulticastAddr(InetAddress inetAddr) {

        if (inetAddr == null) {
            return null;
        }

        try {

            if (inetAddr instanceof Inet6Address) {
                return InetAddress.getByName(IPV6_MCAST_ADDR);
            }

            if (inetAddr instanceof Inet4Address) {
                return InetAddress.getByName(IPV4_MCAST_ADDR);
            }
        } catch (UnknownHostException e) {
            return null;
        }

        return null;
    }

    public static InetAddress getBroadcastAddr(InetAddress inetAddr) {

        if (inetAddr == null) {
            return null;
        }

        if (inetAddr instanceof Inet6Address){
            return null;
        }

        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddr);

            if (ni.isLoopback()) {
                return null;
            }

            for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                if (inetAddr.equals(interfaceAddress.getAddress())) {
                    Log.d("network.util", interfaceAddress.toString());
                    return interfaceAddress.getBroadcast();
                }
            }
        } catch (SocketException e) {
            return null;
        }
        return null;
    }

}
