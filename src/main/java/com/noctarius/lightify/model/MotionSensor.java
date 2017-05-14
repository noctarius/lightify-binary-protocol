package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;

public class MotionSensor
        extends Device {

    MotionSensor(Address address, String firmware, String name) {
        super(address, firmware, name);
    }

    @Override
    public void update(GetDeviceInfoResponse deviceUpdate) {
    }

    @Override
    public String toString() {
        return "MotionSensor{" + "address=" + getAddress() + ", firmware='" + getFirmware() + '\'' + ", name='" + getName() + '\''
                + '}';
    }
}
