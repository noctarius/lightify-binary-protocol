package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.ReadablePacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class GetZoneInfoResponse
        extends AbstractPacket
        implements ReadablePacket {

    private final int zoneId;
    private final String name;
    private final List<Address> devices;

    GetZoneInfoResponse(ByteBuffer buffer) {
        super(buffer);
        this.zoneId = Short.toUnsignedInt(buffer.getShort());
        this.name = readString(16, buffer);
        this.devices = readDevices(buffer);
    }

    @Override
    public void accept(Consumer<? super ReadablePacket> handler) {
        handler.accept(this);
    }

    public Iterable<Address> getDevices() {
        return devices;
    }

    private List<Address> readDevices(ByteBuffer buffer) {
        int numOfDevices = Byte.toUnsignedInt(buffer.get());

        List<Address> devices = new ArrayList<>();
        for (int i = 0; i < numOfDevices; i++) {
            devices.add(readAddress(buffer));
        }
        return Collections.unmodifiableList(devices);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GetZoneInfoResponse)) {
            return false;
        }

        GetZoneInfoResponse that = (GetZoneInfoResponse) o;

        if (zoneId != that.zoneId) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return devices != null ? devices.equals(that.devices) : that.devices == null;
    }

    @Override
    public int hashCode() {
        int result = zoneId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (devices != null ? devices.hashCode() : 0);
        return result;
    }
}
