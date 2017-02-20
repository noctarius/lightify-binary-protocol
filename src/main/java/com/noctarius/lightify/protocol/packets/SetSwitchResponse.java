package com.noctarius.lightify.protocol.packets;

import java.nio.ByteBuffer;

public final class SetLuminanceResponse extends AbstractLightResponse {

    SetLuminanceResponse(ByteBuffer buffer) {
        super(buffer);
    }
}
