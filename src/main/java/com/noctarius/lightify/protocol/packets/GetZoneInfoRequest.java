package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;

public final class GetZoneInfoRequest extends AbstractAddressableRequest {

    public GetZoneInfoRequest(Address address, long requestId) {
        super(address, 0, Command.ZONE_INFO, requestId);
    }
}
