package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.model.Addressable;
import com.noctarius.lightify.model.ColorLight;
import com.noctarius.lightify.model.DimmableLight;
import com.noctarius.lightify.model.Switchable;
import com.noctarius.lightify.model.TunableWhiteLight;
import com.noctarius.lightify.model.Zone;
import com.noctarius.lightify.protocol.WritablePacket;

import java.util.function.Supplier;

public class PacketFactory {

    private final Supplier<Long> requestIdSupplier;

    public PacketFactory(Supplier<Long> requestIdSupplier) {
        this.requestIdSupplier = requestIdSupplier;
    }

    public WritablePacket setColor(ColorLight light, short red, short green, short blue, int millis) {
        long requestId = requestIdSupplier.get();
        return new SetColorRequest(red, green, blue, (short) 0xFF, millis, light.getAddress(), requestId);
    }

    public WritablePacket setTemperature(TunableWhiteLight light, int temperature, int millis) {
        long requestId = requestIdSupplier.get();
        return new SetTemperatureRequest(temperature, millis, light.getAddress(), requestId);
    }

    public WritablePacket setLuminance(DimmableLight light, short luminance, int millis) {
        long requestId = requestIdSupplier.get();
        return new SetLuminanceRequest(luminance, millis, light.getAddress(), requestId);
    }

    public WritablePacket setSwitch(Switchable addressable, boolean on) {
        long requestId = requestIdSupplier.get();
        return new SetSwitchRequest(on, addressable.getAddress(), requestId);
    }

    public WritablePacket setSoftSwitch(Switchable addressable, boolean on, int millis) {
        long requestId = requestIdSupplier.get();
        return new SetSoftSwitchRequest(on, millis, addressable.getAddress(), requestId);
    }

    public WritablePacket listZones() {
        long requestId = requestIdSupplier.get();
        return new ZoneListRequest(requestId);
    }

    public WritablePacket listDevices() {
        long requestId = requestIdSupplier.get();
        return new DeviceListRequest(requestId);
    }

    public WritablePacket deviceInfo(Addressable addressable) {
        long requestId = requestIdSupplier.get();
        return new GetDeviceInfoRequest(addressable.getAddress(), requestId);
    }

    public WritablePacket zoneInfo(Zone zone) {
        long requestId = requestIdSupplier.get();
        return new GetZoneInfoRequest(zone.getAddress(), requestId);
    }
}
