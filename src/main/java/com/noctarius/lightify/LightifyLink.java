package com.noctarius.lightify;

import com.noctarius.lightify.model.Capability;
import com.noctarius.lightify.model.ColorLight;
import com.noctarius.lightify.model.Device;
import com.noctarius.lightify.model.DimmableLight;
import com.noctarius.lightify.model.LightifyModel;
import com.noctarius.lightify.model.Luminary;
import com.noctarius.lightify.model.Switchable;
import com.noctarius.lightify.model.TunableWhiteLight;
import com.noctarius.lightify.model.Zone;
import com.noctarius.lightify.protocol.Lightify;
import com.noctarius.lightify.protocol.packets.AbstractPacket;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.noctarius.lightify.protocol.LightifyUtils.exceptional;

public class LightifyLink {

    private final Map<String, Device> devices = new ConcurrentHashMap<>();
    private final Map<String, Zone> zones = new ConcurrentHashMap<>();

    private final Lightify lightify;

    public LightifyLink(String address) {
        this.lightify = exceptional(() -> new Lightify(InetAddress.getByName(address)));
    }

    public Device findDevice(String address) {
        return devices.get(address);
    }

    public Zone findZone(String zoneId) {
        return zones.get(zoneId);
    }

    public void performSearch(Consumer<Device> consumer) {
        lightify.devices((response) -> {
            for (AbstractPacket.Device d : response.getDevices()) {
                Device device = LightifyModel.createLightifyDevice(d);
                if (device != null) {
                    String address = DatatypeConverter.printHexBinary(d.getAddress().getAddress());
                    devices.put(address, device);
                    consumer.accept(device);
                }
            }

            performZoneSearch(consumer);
        });
    }

    private void performZoneSearch(Consumer<Device> consumer) {
        lightify.zones((response) -> {
            for (AbstractPacket.Zone z : response.getZones()) {
                Zone zone = LightifyModel.createLightifyZone(z);
                if (zone != null) {
                    String zoneId = getZoneUID(z.getZoneId());
                    zones.put(zoneId, zone);
                    performZoneInfo(zone, consumer);
                }
            }
        });
    }

    public void performStatusUpdate(Device device, Consumer<Device> consumer) {
        lightify.deviceInfo(device, (response) -> {
            device.update(response);
            consumer.accept(device);
        });
    }

    void performSwitch(Luminary luminary, boolean activate, Consumer<Luminary> consumer) {
        if (luminary.hasCapability(Capability.SoftSwitchable)) {
            Switchable switchable = luminary.asSwitchable();
            lightify.softSwitch(switchable, activate, 200, (response) -> {
                switchable.setOn(activate);
                consumer.accept(luminary);
            });
        } else {
            Switchable switchable = luminary.asSwitchable();
            lightify.hardSwitch(switchable, activate, (response) -> {
                switchable.setOn(activate);
                consumer.accept(luminary);
            });
        }
    }

    void performLuminance(Luminary luminary, byte luminance, short millis, Consumer<Luminary> consumer) {
        if (luminary.hasCapability(Capability.Dimmable)) {
            DimmableLight light = luminary.asDimmableLight();
            lightify.luminance(light, luminance, millis, (response) -> {
                light.setLuminance(luminance);
                consumer.accept(luminary);
            });
        }
    }

    void performRGB(Luminary luminary, byte r, byte g, byte b, short millis, Consumer<Luminary> consumer) {
        if (luminary.hasCapability(Capability.RGB)) {
            ColorLight light = luminary.asColorLight();
            lightify.rgb(light, r, g, b, millis, (response) -> {
                light.setRed(r);
                light.setGreen(g);
                light.setBlue(b);
                consumer.accept(luminary);
            });
        }
    }

    void performTemperature(Luminary luminary, short temperature, short millis, Consumer<Luminary> consumer) {
        if (luminary.hasCapability(Capability.TunableWhite)) {
            TunableWhiteLight light = luminary.asTunableWhiteLight();
            lightify.temperature(light, temperature, millis, (response) -> {
                light.setTemperature(temperature);
                consumer.accept(luminary);
            });
        }
    }

    void performZoneInfo(Zone zone, Consumer<Device> consumer) {
        lightify.zoneInfo(zone, (response) -> {
            zone.update(response);
            consumer.accept(zone);
        });
    }

    public void disconnect() {
        lightify.dispose();
    }

    private String getZoneUID(int zoneId) {
        return "zone::" + zoneId;
    }
}
