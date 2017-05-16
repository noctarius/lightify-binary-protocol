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
import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.Lightify;
import com.noctarius.lightify.protocol.packets.AbstractPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.noctarius.lightify.protocol.LightifyUtils.exceptional;

public class LightifyLink {

    private final Logger logger = LoggerFactory.getLogger(Lightify.class);

    private final Map<String, Device> devices = new ConcurrentHashMap<>();
    private final Map<String, Zone> zones = new ConcurrentHashMap<>();

    private final Lightify lightify;

    public LightifyLink(String address, StatusListener statusListener) {
        this.lightify = exceptional(() -> new Lightify(InetAddress.getByName(address), statusListener));
    }

    public Device findDevice(String address) {
        return devices.get(address);
    }

    public Zone findZone(String address) {
        return zones.get(address);
    }

    public void performSearch(Consumer<Device> consumer) {
        performDeviceSearch(consumer, () -> performZoneSearch(consumer));
    }

    public void performDeviceSearch(Consumer<Device> consumer) {
        performDeviceSearch(consumer, null);
    }

    public void performZoneSearch(Consumer<Device> consumer) {
        lightify.zones((response) -> {
            for (AbstractPacket.Zone z : response.getZones()) {
                Zone zone = LightifyModel.createLightifyZone(z);
                String zoneId = zone.getAddress().toAddressCode();
                logger.debug("Found zone: {}", zoneId);
                zones.put(zoneId, zone);
                performZoneInfo(zone, consumer);
            }
        });
    }

    public void performStatusUpdate(Device device, Consumer<Device> consumer) {
        lightify.deviceInfo(device, (response) -> {
            device.update(response);
            consumer.accept(device);
        });
    }

    public void performSwitch(Switchable switchable, boolean activate, Consumer<Switchable> consumer) {
        performSwitch(switchable, activate, 0, consumer);
    }

    public void performSwitch(Switchable switchable, boolean activate, int millis, Consumer<Switchable> consumer) {
        if (millis > 65335) {
            throw new IllegalArgumentException("millis cannot be larger than 65535");
        }
        boolean softswitch = false;
        if (switchable instanceof Luminary) {
            softswitch = millis > 0 && ((Luminary) switchable).hasCapability(Capability.SoftSwitchable);
        }
        if (softswitch) {
            lightify.softSwitch(switchable, activate, millis, (response) -> {
                switchable.setOn(activate);
                consumer.accept(switchable);
            });
        } else {
            lightify.hardSwitch(switchable, activate, (response) -> {
                switchable.setOn(activate);
                consumer.accept(switchable);
            });
        }
    }

    public void performLuminance(Luminary luminary, byte luminance, int millis, Consumer<Luminary> consumer) {
        if (millis > 65335) {
            throw new IllegalArgumentException("millis cannot be larger than 65535");
        }
        if (luminary.hasCapability(Capability.Dimmable)) {
            DimmableLight light = luminary.asDimmableLight();
            lightify.luminance(light, luminance, millis, (response) -> {
                light.setLuminance(luminance);
                consumer.accept(luminary);
            });
        }
    }

    public void performRGB(Luminary luminary, int red, int green, int blue, int millis, Consumer<Luminary> consumer) {
        if (red > 255 || red < 0) {
            throw new IllegalArgumentException("red must be in range [0..255]");
        }
        if (green > 255 || green < 0) {
            throw new IllegalArgumentException("green must be in range [0..255]");
        }
        if (blue > 255 || blue < 0) {
            throw new IllegalArgumentException("blue must be in range [0..255]");
        }
        if (millis > 65335) {
            throw new IllegalArgumentException("millis cannot be larger than 65535");
        }

        if (luminary.hasCapability(Capability.RGB)) {
            ColorLight light = luminary.asColorLight();
            lightify.rgb(light, (short) red, (short) green, (short) blue, millis, (response) -> {
                light.setRed((short) red);
                light.setGreen((short) green);
                light.setBlue((short) blue);
                consumer.accept(luminary);
            });
        }
    }

    public void performTemperature(Luminary luminary, int temperature, int millis, Consumer<Luminary> consumer) {
        if (luminary.hasCapability(Capability.TunableWhite)) {
            TunableWhiteLight light = luminary.asTunableWhiteLight();
            lightify.temperature(light, temperature, millis, (response) -> {
                light.setTemperature(temperature);
                consumer.accept(luminary);
            });
        }
    }

    public void performZoneInfo(Zone zone, Consumer<Device> consumer) {
        lightify.zoneInfo(zone, (response) -> {
            zone.update(response);
            if (consumer != null) {
                consumer.accept(zone);
            }
        });
    }

    public void disconnect() {
        lightify.dispose();
    }

    private void performDeviceSearch(Consumer<Device> consumer, Runnable finishAction) {
        lightify.devices((response) -> {
            for (AbstractPacket.Device d : response.getDevices()) {
                Device device = LightifyModel.createLightifyDevice(d);
                if (device != null) {
                    String address = d.getAddress().toAddressCode();
                    logger.debug("Found device: {}", address);
                    devices.put(address, device);

                    if (consumer != null) {
                        consumer.accept(device);
                    }
                }
            }

            if (finishAction != null) {
                finishAction.run();
            }
        });
    }
}
