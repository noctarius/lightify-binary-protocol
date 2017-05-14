package com.noctarius.lightify.protocol.packets;

import java.nio.ByteBuffer;

public final class SetSoftSwitchResponse
        extends AbstractLightResponse {

    SetSoftSwitchResponse(ByteBuffer buffer) {
        super(buffer);
    }
}
