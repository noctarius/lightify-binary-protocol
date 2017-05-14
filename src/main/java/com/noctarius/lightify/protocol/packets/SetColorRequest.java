package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;

import java.nio.ByteBuffer;

public final class SetColorRequest extends AbstractAddressableRequest {

    private final short red;
    private final short green;
    private final short blue;
    private final short alpha;
    private final int millis;

    protected SetColorRequest(short red, short green, short blue, short alpha, int millis, Address address, long requestId) {
        super(address, 6, Command.LIGHT_COLOR, requestId);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.millis = millis;
    }

    public short getRed() {
        return red;
    }

    public short getGreen() {
        return green;
    }

    public short getBlue() {
        return blue;
    }

    public short getAlpha() {
        return alpha;
    }

    public int getMillis() {
        return millis;
    }

    @Override
    protected void writePayload(ByteBuffer buffer) {
        buffer.put((byte) red);
        buffer.put((byte) green);
        buffer.put((byte) blue);
        buffer.put((byte) alpha);
        buffer.putShort((short) millis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetColorRequest)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SetColorRequest that = (SetColorRequest) o;

        if (red != that.red) {
            return false;
        }
        if (green != that.green) {
            return false;
        }
        if (blue != that.blue) {
            return false;
        }
        if (alpha != that.alpha) {
            return false;
        }
        return millis == that.millis;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) red;
        result = 31 * result + (int) green;
        result = 31 * result + (int) blue;
        result = 31 * result + (int) alpha;
        result = 31 * result + millis;
        return result;
    }
}
