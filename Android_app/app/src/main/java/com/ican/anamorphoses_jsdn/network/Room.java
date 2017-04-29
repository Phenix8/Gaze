package com.ican.anamorphoses_jsdn.network;

import java.net.InetAddress;

/**
 * Created by root on 14/04/2017.
 */

public class Room {

    private InetAddress address;
    private String name;

    public Room(InetAddress address, String name) {
        this.address = address;
        this.name = name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", this.getName(), this.getAddress().toString());
    }
}
