package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.ReadablePacket;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public final class GetDeviceInfoResponse
        extends AbstractPacket
        implements ReadablePacket {

    private final int deviceId;
    private final Address address;
    private final short reachable;
    private final short unk2;
    private final boolean on;
    private final short luminance;
    private final int temperature;
    private final short red;
    private final short green;
    private final short blue;
    private final short alpha;
    private final short unk3;
    private final short unk4;
    private final short unk5;

    GetDeviceInfoResponse(ByteBuffer buffer) {
        super(buffer);
        this.deviceId = Short.toUnsignedInt(buffer.getShort());
        this.address = readAddress(buffer);
        this.reachable = (short) Byte.toUnsignedInt(buffer.get());
        if (reachable == 0x00) {
            this.unk2 = (short) Byte.toUnsignedInt(buffer.get());
            this.on = buffer.get() != 0;
            this.luminance = (short) Byte.toUnsignedInt(buffer.get());
            this.temperature = Short.toUnsignedInt(buffer.getShort());
            this.red = (short) Byte.toUnsignedInt(buffer.get());
            this.green = (short) Byte.toUnsignedInt(buffer.get());
            this.blue = (short) Byte.toUnsignedInt(buffer.get());
            this.alpha = (short) Byte.toUnsignedInt(buffer.get());
            this.unk3 = (short) Byte.toUnsignedInt(buffer.get());
            this.unk4 = (short) Byte.toUnsignedInt(buffer.get());
            this.unk5 = (short) Byte.toUnsignedInt(buffer.get());
        } else {
            this.unk2 = 0;
            this.on = false;
            this.luminance = 0;
            this.temperature = 0;
            this.red = 0;
            this.green = 0;
            this.blue = 0;
            this.alpha = 0;
            this.unk3 = 0;
            this.unk4 = 0;
            this.unk5 = 0;
        }
    }

    public int getDeviceId() {
        return deviceId;
    }

    public Address getAddress() {
        return address;
    }

    public short getReachable() {
        return reachable;
    }

    public short getUnk2() {
        return unk2;
    }

    public boolean isOn() {
        return on;
    }

    public short getLuminance() {
        return luminance;
    }

    public int getTemperature() {
        return temperature;
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

    public short getUnk3() {
        return unk3;
    }

    public short getUnk4() {
        return unk4;
    }

    public short getUnk5() {
        return unk5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GetDeviceInfoResponse)) {
            return false;
        }

        GetDeviceInfoResponse that = (GetDeviceInfoResponse) o;

        if (deviceId != that.deviceId) {
            return false;
        }
        if (reachable != that.reachable) {
            return false;
        }
        if (unk2 != that.unk2) {
            return false;
        }
        if (on != that.on) {
            return false;
        }
        if (luminance != that.luminance) {
            return false;
        }
        if (temperature != that.temperature) {
            return false;
        }
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
        if (unk3 != that.unk3) {
            return false;
        }
        if (unk4 != that.unk4) {
            return false;
        }
        if (unk5 != that.unk5) {
            return false;
        }
        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        int result = deviceId;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (int) reachable;
        result = 31 * result + (int) unk2;
        result = 31 * result + (on ? 1 : 0);
        result = 31 * result + (int) luminance;
        result = 31 * result + temperature;
        result = 31 * result + (int) red;
        result = 31 * result + (int) green;
        result = 31 * result + (int) blue;
        result = 31 * result + (int) alpha;
        result = 31 * result + (int) unk3;
        result = 31 * result + (int) unk4;
        result = 31 * result + (int) unk5;
        return result;
    }

    @Override
    public void accept(Consumer<? super ReadablePacket> handler) {
        handler.accept(this);
    }
}
