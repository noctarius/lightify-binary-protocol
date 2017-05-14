package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;

import java.util.Set;

public class LuminaryDevice
        extends Device
        implements Luminary, Switchable, DimmableLight, ColorLight, TunableWhiteLight {

    private final Set<Capability> capabilities;

    private boolean on;
    private short luminance;
    private short red;
    private short green;
    private short blue;
    private int temperature;

    LuminaryDevice(Set<Capability> capabilities, Address address, String firmware, String name) {
        super(address, firmware, name);
        this.capabilities = capabilities;
    }

    @Override
    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    @Override
    public boolean hasCapability(Capability capability) {
        return capabilities.contains(capability);
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
    public short getLuminance() {
        return luminance;
    }

    @Override
    public void setLuminance(short luminance) {
        this.luminance = luminance;
    }

    @Override
    public short getRed() {
        return red;
    }

    @Override
    public void setRed(short red) {
        this.red = red;
    }

    @Override
    public short getGreen() {
        return green;
    }

    @Override
    public void setGreen(short green) {
        this.green = green;
    }

    @Override
    public short getBlue() {
        return blue;
    }

    @Override
    public void setBlue(short blue) {
        this.blue = blue;
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public Switchable asSwitchable() {
        if (!hasCapability(Capability.Switchable)) {
            throw new IllegalStateException("Not a switchable light");
        }
        return this;
    }

    @Override
    public DimmableLight asDimmableLight() {
        if (!hasCapability(Capability.Dimmable)) {
            throw new IllegalStateException("Not a dimmable light");
        }
        return this;
    }

    @Override
    public ColorLight asColorLight() {
        if (!hasCapability(Capability.RGB)) {
            throw new IllegalStateException("Not a color light");
        }
        return this;
    }

    @Override
    public TunableWhiteLight asTunableWhiteLight() {
        if (!hasCapability(Capability.TunableWhite)) {
            throw new IllegalStateException("Not a tunable white light");
        }
        return this;
    }

    @Override
    public void update(GetDeviceInfoResponse deviceUpdate) {
        super.update(deviceUpdate);
        this.on = deviceUpdate.isOn();
        this.luminance = deviceUpdate.getLuminance();
        this.red = deviceUpdate.getRed();
        this.green = deviceUpdate.getGreen();
        this.blue = deviceUpdate.getBlue();
        this.temperature = deviceUpdate.getTemperature();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Luminary{" + "capabilities=" + capabilities);

        if (hasCapability(Capability.Switchable)) {
            sb.append(", on=").append(on);
        }

        if (hasCapability(Capability.Dimmable)) {
            sb.append(", luminance=").append(luminance);
        }

        if (hasCapability(Capability.RGB)) {
            sb.append(", red=").append(red).append(", green=").append(green).append(", blue=").append(blue);
        }

        if (hasCapability(Capability.TunableWhite)) {
            sb.append(", temperature=").append(temperature);
        }
        return sb.append(", address=").append(getAddress()).append(", firmware='").append(getFirmware()).append('\'')
                 .append(", name='").append(getName()).append('\'').append('}').toString();
    }
}
