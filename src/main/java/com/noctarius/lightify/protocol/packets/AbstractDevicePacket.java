package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.Command;
import com.noctarius.lightify.protocol.Status;

import java.nio.ByteBuffer;

public abstract class AbstractZonePacket extends AbstractPacket {

    private static final byte[] ADDRESSING_FILLER = new byte[6];

    protected final int zoneId;

    protected AbstractZonePacket(int zoneId, Command command) {
        super((short) 0x0, command);
        this.zoneId = zoneId;
    }

    protected AbstractZonePacket(int zoneId, int length, Command command, long requestId, Status status) {
        super(length, (short) 0x0, command, requestId, status);
        this.zoneId = zoneId;
    }

    @Override
    protected void buildAddressing(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) zoneId);
        byteBuffer.put(ADDRESSING_FILLER);
    }
}
