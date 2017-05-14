package com.noctarius.lightify.protocol.packets;

import java.nio.ByteBuffer;

public final class SetSwitchResponse extends AbstractLightResponse {

    SetSwitchResponse(ByteBuffer buffer) {
        super(buffer);
    }
}
