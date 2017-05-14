package com.noctarius.lightify.protocol;

import com.noctarius.lightify.protocol.packets.AbstractPacket;
import com.noctarius.lightify.protocol.packets.DeviceListResponse;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;
import com.noctarius.lightify.protocol.packets.GetZoneInfoResponse;
import com.noctarius.lightify.protocol.packets.SetColorResponse;
import com.noctarius.lightify.protocol.packets.SetLuminanceResponse;
import com.noctarius.lightify.protocol.packets.SetSoftSwitchResponse;
import com.noctarius.lightify.protocol.packets.SetSwitchResponse;
import com.noctarius.lightify.protocol.packets.SetTemperatureResponse;
import com.noctarius.lightify.protocol.packets.ZoneListResponse;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

public enum Command {
    /**
     * Retrieves information and status about all paired devices
     */
    DEVICE_LIST(0x13, true, DeviceListResponse.class),

    /**
     * Retrieves information about a single paired device
     */
    DEVICE_INFO(0x68, false, GetDeviceInfoResponse.class),

    /**
     * Retrieves information about all configured zones (group of devices)
     */
    ZONE_LIST(0x1E, true, ZoneListResponse.class),

    /**
     * Retrieves information about a single configured zone
     */
    ZONE_INFO(0x26, false, GetZoneInfoResponse.class),

    /**
     * Reconfigures the luminance of the addressed device or zone
     */
    LIGHT_LUMINANCE(0x31, false, SetLuminanceResponse.class),

    /**
     * Reconfigures the power on/off status of the addressed device or zone
     */
    LIGHT_SWITCH(0x32, false, SetSwitchResponse.class),

    /**
     * Reconfigures the white light temperature of the addressed device or zone
     */
    LIGHT_TEMPERATURE(0x33, false, SetTemperatureResponse.class),

    /**
     * Reconfigures the RGB color of the addressed device or zone
     */
    LIGHT_COLOR(0x36, false, SetColorResponse.class),

    LIGHT_SOFT_SWITCH_ON(0xDB, false, SetSoftSwitchResponse.class),

    LIGHT_SOFT_SWITCH_OFF(0xDC, false, SetSoftSwitchResponse.class),

    SCAN_WIFI_CONFIG(0xE3, true, GetZoneInfoResponse.class);

    private static final Command[] VALUES = values();

    private final byte id;
    private final boolean broadcast;
    private final Constructor constructor;

    Command(int id, boolean broadcast, Class<? extends AbstractPacket> packetType) {
        this.id = (byte) id;
        this.broadcast = broadcast;
        this.constructor = findDefaultConstructor(packetType);
    }

    public byte getId() {
        return id;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    @SuppressWarnings({"unchecked"})
    public <P extends AbstractPacket> P newPacketInstance(ByteBuffer buffer) {
        try {
            return (P) constructor.newInstance(buffer);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not instantiate new packet", e);
        }
    }

    private Constructor findDefaultConstructor(Class<? extends AbstractPacket> packetType) {
        try {
            Constructor defaultConstructor = packetType.getDeclaredConstructor(ByteBuffer.class);
            defaultConstructor.setAccessible(true);
            return defaultConstructor;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not find default constructor", e);
        }
    }

    public static Command byId(byte id) {
        for (Command command : VALUES) {
            if (command.getId() == id) {
                return command;
            }
        }
        throw new IllegalArgumentException("Unknown command found: 0x" + Integer.toHexString(Byte.toUnsignedInt(id)));
    }

    public static Command byId(int id) {
        return byId((byte) id);
    }
}
