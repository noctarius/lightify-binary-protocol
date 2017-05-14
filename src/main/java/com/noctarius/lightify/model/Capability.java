package com.noctarius.lightify.model;

/**
 * This enum class defines the basic capabilities of Lightify devices based on the
 * Type ID (or based on "I know better", like for IKEA Tradfri Bulbs).
 *
 * @author Christoph Engelbert (@noctarius2k) - Initial contribution
 */
public enum Capability {

    /**
     * The device supports dimming.
     */
    Dimmable,

    /**
     * The device supports multiple white temperature states (seamless or fixed points).
     */
    TunableWhite,

    /**
     * The device is simple pure white.
     */
    PureWhite,

    /**
     * The device has support for changing colors in the RGB space.
     */
    RGB,

    /**
     * Yet Unknown.
     */
    Unk2,

    /**
     * The device supports motion events.
     */
    MotionSensor,

    /**
     * The device is switchable.
     */
    Switchable,

    /**
     * The device is soft switchable (fading).
     */
    SoftSwitchable,

    /**
     * Yes Unknown
     */
    Unk3
}
