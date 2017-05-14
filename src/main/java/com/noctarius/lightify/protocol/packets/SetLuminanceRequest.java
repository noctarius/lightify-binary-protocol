package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;

import java.nio.ByteBuffer;

public final class SetLuminanceRequest extends AbstractAddressableRequest {

    private final short luminance;
    private final int millis;

    public SetLuminanceRequest(short luminance, int millis, Address address, long requestId) {
        super(address, 3, Command.LIGHT_LUMINANCE, requestId);
        this.luminance = luminance;
        this.millis = millis;
    }

    public short getLuminance() {
        return luminance;
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
        buffer.put((byte) luminance);
        buffer.putShort((short) millis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetLuminanceRequest)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SetLuminanceRequest that = (SetLuminanceRequest) o;

        if (luminance != that.luminance) {
            return false;
        }
        return millis == that.millis;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) luminance;
        result = 31 * result + millis;
        return result;
    }
}
