package com.noctarius.lightify;

public enum Command {
    TEST(0x0E, true),

    /**
     * Retrieves information and status about all paired devices
     */
    STATUS_ALL(0x13, true),

    /**
     * Retrieves information about a single paired device
     */
    STATUS_SINGLE(0x68, false),

    /**
     * Retrieves information about all configured zones (group of devices)
     */
    ZONE_LIST(0x1E, true),

    /**
     * Retrieves information about a single configured zone
     */
    ZONE_INFO(0x26, false),

    /**
     * Reconfigures the luminance of the addressed device or zone
     */
    LIGHT_LUMINANCE(0x31, false),

    /**
     * Reconfigures the power on/off status of the addressed device or zone
     */
    LIGHT_SWITCH(0x32, false),

    /**
     * Reconfigures the white light temperature of the addressed device or zone
     */
    LIGHT_TEMPERATURE(0x33, false),

    /**
     * Reconfigures the RGB color of the addressed device or zone
     */
    LIGHT_COLOR(0x36, false);

    private final byte id;
    private final boolean zone;

    Command(int id, boolean zone) {
        this.id = (byte) id;
        this.zone = zone;
    }

    public byte getId() {
        return id;
    }

    public boolean isZone() {
        return zone;
    }
}
