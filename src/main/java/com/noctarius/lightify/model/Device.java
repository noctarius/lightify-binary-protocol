package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;

public abstract class LightifyDevice implements Addressable {

    private final Address address;

    protected LightifyDevice(Address address) {
        this.address = address;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LightifyDevice)) {
            return false;
        }

        LightifyDevice that = (LightifyDevice) o;

        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }
}
