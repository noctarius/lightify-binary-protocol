package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;

import java.nio.ByteBuffer;

public final class SetTemperatureRequest extends AbstractAddressableRequest {

    private final int temperature;
    private final int millis;

    SetTemperatureRequest(int temperature, int millis, Address address, long requestId) {
        super(address, 4, Command.LIGHT_TEMPERATURE, requestId);
        this.temperature = temperature;
        this.millis = millis;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getMillis() {
        return millis;
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
        buffer.putShort((short) temperature);
        buffer.putShort((short) millis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetTemperatureRequest)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SetTemperatureRequest that = (SetTemperatureRequest) o;

        if (temperature != that.temperature) {
            return false;
        }
        return millis == that.millis;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + temperature;
        result = 31 * result + millis;
        return result;
    }
}
