package com.noctarius.lightify.protocol;

public interface Packet {

    int getPacketLength();

    short getFlag();

    Command getCommand();

    long getRequestId();

    Status getStatus();
}
