package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;

import java.util.Set;

public interface Luminary
        extends Addressable {

    Address getAddress();

    String getName();

    String getFirmware();

    boolean isOn();

    Set<Capability> getCapabilities();

    boolean hasCapability(Capability capability);

    DimmableLight asDimmableLight();

    ColorLight asColorLight();

    TunableWhiteLight asTunableWhiteLight();

    Switchable asSwitchable();

}
