package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.Command;
import com.noctarius.lightify.protocol.model.Address;

import java.nio.ByteBuffer;

public abstract class AbstractAddressablePacket extends AbstractPacket {

    private final Address address;

    protected AbstractAddressablePacket(Address address, int packetLength, Command command, long requestId) {
        super(packetLength + 8, (short) 0x0, command, requestId);
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    protected void writeAddress(ByteBuffer byteBuffer) {
        byteBuffer.put(address.getAddress());
    }
}
