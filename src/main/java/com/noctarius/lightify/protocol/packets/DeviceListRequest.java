package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.Command;

public final class ListDevicesRequest extends AbstractBroadcastPacket {
    ListDevicesRequest(long requestId) {
        super(1, Command.STATUS_ALL, requestId);
    }
}
