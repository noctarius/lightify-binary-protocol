package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;

import java.nio.ByteBuffer;

public final class SetSoftSwitchRequest
        extends AbstractAddressableRequest {

    private final boolean on;
    private final int millis;

    SetSoftSwitchRequest(boolean on, int millis, Address address, long requestId) {
        super(address, 2, on ? Command.LIGHT_SOFT_SWITCH_ON : Command.LIGHT_SOFT_SWITCH_OFF, requestId);
        this.on = on;
        this.millis = millis;
    }

    public boolean isOn() {
        return on;
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
        buffer.putShort((short) millis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetSoftSwitchRequest)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SetSoftSwitchRequest that = (SetSoftSwitchRequest) o;

        if (on != that.on) {
            return false;
        }
        return millis == that.millis;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (on ? 1 : 0);
        result = 31 * result + millis;
        return result;
    }
}
