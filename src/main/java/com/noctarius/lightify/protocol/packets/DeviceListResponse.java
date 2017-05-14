package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.ReadablePacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class DeviceListResponse extends AbstractPacket implements ReadablePacket {

    private final List<Device> devices;

    protected DeviceListResponse(ByteBuffer buffer) {
        super(buffer);
        this.devices = readDevices(buffer);
    }

    public Iterable<Device> getDevices() {
        return devices;
    }

    @Override
    public void accept(Consumer<? super ReadablePacket> handler) {
        handler.accept(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeviceListResponse)) {
            return false;
        }

        DeviceListResponse that = (DeviceListResponse) o;

        return devices != null ? devices.equals(that.devices) : that.devices == null;
    }

    @Override
    public int hashCode() {
        return devices != null ? devices.hashCode() : 0;
    }

    private List<Device> readDevices(ByteBuffer buffer) {
        int numOfDevices = Short.toUnsignedInt(buffer.getShort());

        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < numOfDevices; i++) {
            devices.add(readDevice(buffer));
        }
        return Collections.unmodifiableList(devices);
    }
}
