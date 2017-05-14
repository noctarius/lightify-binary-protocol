package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;

public class PowerSocket
        extends Device
        implements Switchable {

    private boolean on;

    PowerSocket(Address address, String firmware, String name) {
        super(address, firmware, name);
    }

    @Override
    public boolean isOn() {
        return on;
    }

    @Override
    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public void update(GetDeviceInfoResponse deviceUpdate) {
        this.on = deviceUpdate.isOn();
    }

    @Override
    public String toString() {
        return "PowerSocket{" + "on=" + on + ", address=" + getAddress() + ", firmware='" + getFirmware() + '\'' + ", name='"
                + getName() + '\'' + '}';
    }
}
