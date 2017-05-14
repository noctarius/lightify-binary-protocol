package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.WritablePacket;

import java.nio.ByteBuffer;

public abstract class AbstractBroadcastRequest extends AbstractPacket implements WritablePacket {

    protected AbstractBroadcastRequest(int packetLength, Command command, long requestId) {
        super(packetLength, (short) 0x0, command, requestId);
    }

    @Override
    protected final void writeAddress(ByteBuffer byteBuffer) {
    }
}
