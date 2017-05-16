package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;
import com.noctarius.lightify.protocol.packets.GetZoneInfoResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Zone
        extends Device {

    private final int zoneId;

    private Address[] addresses = new Address[0];

    Zone(int zoneId, Address address, String name) {
        super(address, "n/a", name);
        this.zoneId = zoneId;
    }

    public int getZoneId() {
        return zoneId;
    }

    public Iterable<Address> getAddresses() {
        return Arrays.asList(addresses);
    }

    public void update(GetZoneInfoResponse zoneUpdate) {
        List<Address> addresses = new ArrayList<>();
        zoneUpdate.getDevices().forEach(addresses::add);
        this.addresses = addresses.toArray(new Address[addresses.size()]);
    }

    @Override
    public String toString() {
        return "Zone{" + "zoneId=" + zoneId + ", addresses=" + Arrays.toString(addresses) + ", address=" + getAddress() + '\''
                + ", name='" + getName() + '\'' + '}';
    }
}
