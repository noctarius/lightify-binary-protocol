package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;

import java.nio.ByteBuffer;

public final class ZoneListRequest extends AbstractBroadcastRequest {

    ZoneListRequest(long requestId) {
        super(0, Command.ZONE_LIST, requestId);
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
    }
}
