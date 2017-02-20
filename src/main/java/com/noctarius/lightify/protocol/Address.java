package com.noctarius.lightify.protocol.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static com.noctarius.lightify.LightifyUtils.toHexMac;

public final class Address {

    private final byte[] address;

    private Address(byte[] address) {
        this.address = address;
    }

    public byte[] getAddress() {
        // Return copy to prevent accidental changing
        return Arrays.copyOf(address, address.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }

        Address address1 = (Address) o;

        return Arrays.equals(address, address1.address);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(address);
    }

    @Override
    public String toString() {
        return toHexMac(address);
    }

    public static Address fromZoneId(int zoneId) {
        byte[] address = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putShort((short) zoneId).array();
        return new Address(address);
    }

    public static Address fromMac(byte[] address) {
        return new Address(Arrays.copyOf(address, address.length));
    }
}
