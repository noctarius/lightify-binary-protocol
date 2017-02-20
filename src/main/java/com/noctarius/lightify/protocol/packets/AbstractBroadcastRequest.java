package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.Command;

import java.nio.ByteBuffer;

public abstract class AbstractBroadcastPacket extends AbstractPacket {

    protected AbstractBroadcastPacket(int packetLength, Command command, long requestId) {
        super(packetLength, (short) 0x2, command, requestId);
    }

    @Override
    protected final void writeAddress(ByteBuffer byteBuffer) {
    }
}
