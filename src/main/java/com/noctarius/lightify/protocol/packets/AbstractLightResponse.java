package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.ReadablePacket;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public abstract class AbstractLightResponse extends AbstractPacket implements ReadablePacket {

    private final int id;
    private final Address address;

    AbstractLightResponse(ByteBuffer buffer) {
        super(buffer);
        this.id = Short.toUnsignedInt(buffer.getShort());
        this.address = readAddress(buffer);
    }

    public int getId() {
        return id;
    }

    public Address getAddress() {
        return address;
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
        if (!(o instanceof AbstractLightResponse)) {
            return false;
        }

        AbstractLightResponse that = (AbstractLightResponse) o;

        if (id != that.id) {
            return false;
        }
        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
