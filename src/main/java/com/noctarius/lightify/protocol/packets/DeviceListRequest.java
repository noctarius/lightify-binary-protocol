package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;

import java.nio.ByteBuffer;

public final class DeviceListRequest extends AbstractBroadcastRequest {

    public DeviceListRequest(long requestId) {
        super(1, Command.DEVICE_LIST, requestId);
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
        buffer.put((byte) 0x01);
    }
}
