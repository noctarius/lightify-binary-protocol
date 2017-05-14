package com.noctarius.lightify.protocol.packets;

import java.nio.ByteBuffer;

public final class SetTemperatureResponse extends AbstractLightResponse {

    SetTemperatureResponse(ByteBuffer buffer) {
        super(buffer);
    }
}
