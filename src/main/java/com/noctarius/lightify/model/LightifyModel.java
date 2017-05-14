package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.packets.AbstractPacket;

import java.util.HashSet;
import java.util.Set;

public final class LightifyModel {

    private LightifyModel() {
    }

    public static Zone createLightifyZone(AbstractPacket.Zone zone) {
        Address address = Address.fromZoneId(zone.getZoneId());
        return new Zone(zone.getZoneId(), address, zone.getName());
    }

    public static <D extends Device> D createLightifyDevice(AbstractPacket.Device device) {
        byte type = device.getType();
        DeviceType deviceType = DeviceType.findByTypeId(type);

        Address address = device.getAddress();
        String firmware = device.getFirmware();
        String name = device.getName();

        if (deviceType == DeviceType.Switch) {
            return null; // not yet supported
        }

        if (deviceType == DeviceType.MotionSensor) {
            return (D) new MotionSensor(address, firmware, name);
        }

        if (deviceType == DeviceType.PowerSocket) {
            return (D) new PowerSocket(address, firmware, name);
        }

        if (deviceType == DeviceType.Unknown) {
            return null; // unknown type, ignore
        }

        return (D) createLuminaryDevice(device);
    }

    private static Luminary createLuminaryDevice(AbstractPacket.Device device) {
        byte type = device.getType();
        Address address = device.getAddress();
        String firmware = device.getFirmware();
        String name = device.getName();
        int temperature = device.getTemperature();
        short red = device.getRed();
        short green = device.getGreen();
        short blue = device.getBlue();
        boolean on = device.isOn();
        short luminance = device.getLuminance();

        // Bulbs have different capabilities
        Set<Capability> capabilities = new HashSet<>();
        capabilities.add(Capability.Switchable);
        capabilities.add(Capability.Dimmable);

        if (type == 1 || type == 4) {
            capabilities.add(Capability.PureWhite);
        }

        if (type == 2 || type == 10) {
            capabilities.add(Capability.TunableWhite);
        }

        if (type == 2 || type == 4 || type == 10) {
            capabilities.add(Capability.SoftSwitchable);
        }

        if (type == 10) {
            capabilities.add(Capability.RGB);
        }

        Luminary luminary = new LuminaryDevice(capabilities, address, firmware, name);
        if (luminary.hasCapability(Capability.Switchable)) {
            luminary.asSwitchable().setOn(on);
        }

        if (luminary.hasCapability(Capability.Dimmable)) {
            luminary.asDimmableLight().setLuminance(luminance);
        }

        if (luminary.hasCapability(Capability.TunableWhite)) {
            TunableWhiteLight light = luminary.asTunableWhiteLight();
            light.setTemperature(temperature);
        }

        if (luminary.hasCapability(Capability.RGB)) {
            ColorLight light = luminary.asColorLight();
            light.setRed(red);
            light.setGreen(green);
            light.setBlue(blue);
        }

        return luminary;
    }

}
