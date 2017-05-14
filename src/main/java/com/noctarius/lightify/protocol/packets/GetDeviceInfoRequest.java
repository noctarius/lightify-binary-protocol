package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;

public class GetDeviceInfoRequest extends AbstractAddressableRequest {

    public GetDeviceInfoRequest(Address address, long requestId) {
        super(address, 0, Command.DEVICE_INFO, requestId);
    }
}
