package com.noctarius.lightify.protocol;

import java.nio.ByteBuffer;

public interface WritablePacket extends Packet {

    void write(ByteBuffer buffer);
}
