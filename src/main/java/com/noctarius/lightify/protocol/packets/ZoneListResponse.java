package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.ReadablePacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class ZoneListResponse extends AbstractPacket implements ReadablePacket {

    private final List<Zone> zones;

    ZoneListResponse(ByteBuffer buffer) {
        super(buffer);
        this.zones = readZones(buffer);
    }

    public Iterable<Zone> getZones() {
        return zones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZoneListResponse)) {
            return false;
        }

        ZoneListResponse that = (ZoneListResponse) o;

        return zones != null ? zones.equals(that.zones) : that.zones == null;
    }

    @Override
    public int hashCode() {
        return zones != null ? zones.hashCode() : 0;
    }

    private List<Zone> readZones(ByteBuffer buffer) {
        int numOfZones = Short.toUnsignedInt(buffer.getShort());

        List<Zone> zones = new ArrayList<>();
        for (int i = 0; i < numOfZones; i++) {
            zones.add(readZone(buffer));
        }
        return Collections.unmodifiableList(zones);
    }

    @Override
    public void accept(Consumer<? super ReadablePacket> handler) {
        handler.accept(this);
    }
}
