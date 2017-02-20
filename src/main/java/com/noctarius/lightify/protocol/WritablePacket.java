package com.noctarius.lightify.protocol;

import java.nio.ByteBuffer;

public interface WriteablePacket extends Packet {

    void write(ByteBuffer buffer);
}
