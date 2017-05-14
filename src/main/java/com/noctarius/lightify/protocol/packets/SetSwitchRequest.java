package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;

import java.nio.ByteBuffer;

public final class SetSwitchRequest extends AbstractAddressableRequest {

    private final boolean on;

    SetSwitchRequest(boolean on, Address address, long requestId) {
        super(address, 1, Command.LIGHT_SWITCH, requestId);
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
        buffer.put((byte) (on ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetSwitchRequest)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SetSwitchRequest that = (SetSwitchRequest) o;

        return on == that.on;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (on ? 1 : 0);
        return result;
    }
}
