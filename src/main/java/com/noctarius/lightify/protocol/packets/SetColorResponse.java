package com.noctarius.lightify.protocol.packets;

import java.nio.ByteBuffer;

public final class SetColorResponse extends AbstractLightResponse {

    SetColorResponse(ByteBuffer buffer) {
        super(buffer);
    }
}
