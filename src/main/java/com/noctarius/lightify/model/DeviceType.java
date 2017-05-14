package com.noctarius.lightify.model;


/**
 * This enum class represents the supported item types of the binary protocol.
 *
 * @author Christoph Engelbert (@noctarius2k) - Initial contribution
 */
public enum DeviceType {

    /**
     * Unknown item type.
     */
    Unknown(-1),

    /**
     * A Lightify (ZigBee) Light. Each bulb type supports a different set of
     * capabilities which are defined by the {@link Capability} enum.
     */
    Bulb(1, 2, 4, 10),

    /**
     * A simple power socket that can be switched on and off.
     */
    PowerSocket(16),

    /**
     * A motion sensor that can be used to react on motion events.
     */
    MotionSensor(32),

    /**
     * A switch type device. Might have 2 or 4 actual switches.
     */
    Switch(64, 65);

    private static final DeviceType[] DEVICE_TYPES = DeviceType.values();

    private final int[] types;

    DeviceType(int... types) {
        this.types = types;
    }

    public static DeviceType findByTypeId(int type) {
        for (DeviceType deviceType : DEVICE_TYPES) {
            for (int t : deviceType.types) {
                if (t == type) {
                    return deviceType;
                }
            }
        }
        return Unknown;
    }
}
