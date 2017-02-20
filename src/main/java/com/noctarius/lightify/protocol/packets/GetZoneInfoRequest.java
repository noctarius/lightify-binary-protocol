package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.Command;

import java.nio.ByteBuffer;

public class GetZoneInfoRequestPacket extends AbstractAddressablePacket {

    protected GetZoneInfoRequestPacket(int zoneId, int packetLength, Command command, long requestId) {
        super(zoneId, packetLength, command, requestId);
    }

    protected GetZoneInfoRequestPacket(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
    }
}
