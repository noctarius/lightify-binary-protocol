package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.AdresseablePacket;

import java.nio.ByteBuffer;

public abstract class AbstractAddressableRequest extends AbstractPacket implements AdresseablePacket {

    private final Address address;

    protected AbstractAddressableRequest(Address address, int packetLength, Command command, long requestId) {
        super(packetLength + 8, (short) 0x0, command, requestId);
        this.address = address;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    protected final void writeAddress(ByteBuffer byteBuffer) {
        writeAddress(address, byteBuffer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractAddressableRequest)) {
            return false;
        }

        AbstractAddressableRequest that = (AbstractAddressableRequest) o;

        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
