package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;

public abstract class Device
        implements Addressable {

    private final Address address;
    private final String firmware;
    private final String name;

    private boolean reachable;

    protected Device(Address address, String firmware, String name) {
        this.address = address;
        this.firmware = firmware;
        this.name = name;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    public String getFirmware() {
        return firmware;
    }

    public String getName() {
        return name;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void update(GetDeviceInfoResponse deviceUpdate) {
        this.reachable = deviceUpdate.getReachable() == 0x00;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Device)) {
            return false;
        }

        Device device = (Device) o;

        if (address != null ? !address.equals(device.address) : device.address != null) {
            return false;
        }
        if (firmware != null ? !firmware.equals(device.firmware) : device.firmware != null) {
            return false;
        }
        return name != null ? name.equals(device.name) : device.name == null;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (firmware != null ? firmware.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Device{" + "address=" + address + ", firmware='" + firmware + '\'' + ", name='" + name + '\'' + '}';
    }
}
