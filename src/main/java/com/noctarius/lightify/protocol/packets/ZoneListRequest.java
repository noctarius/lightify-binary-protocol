package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.Command;

import java.nio.ByteBuffer;

public class ZoneListRequestPacket extends AbstractBroadcastPacket {

    protected ZoneListRequestPacket(long requestId) {
        super(0, Command.ZONE_LIST, requestId);
    }

    protected ZoneListRequestPacket(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
    }
}
