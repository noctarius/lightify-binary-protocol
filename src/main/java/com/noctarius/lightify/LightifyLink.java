package com.noctarius.lightify;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.noctarius.lightify.Command.LIGHT_COLOR;
import static com.noctarius.lightify.Command.LIGHT_LUMINANCE;
import static com.noctarius.lightify.Command.LIGHT_SWITCH;
import static com.noctarius.lightify.Command.LIGHT_TEMPERATURE;
import static com.noctarius.lightify.Command.STATUS_ALL;
import static com.noctarius.lightify.Command.STATUS_SINGLE;
import static com.noctarius.lightify.Command.ZONE_INFO;
import static com.noctarius.lightify.Command.ZONE_LIST;
import static com.noctarius.lightify.LightifyUtils.exceptional;

public class LightifyLink {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final AtomicInteger sequencer = new AtomicInteger();
    private final Map<String, LightifyLuminary> devices = new ConcurrentHashMap<>();
    private final Map<String, LightifyZone> zones = new ConcurrentHashMap<>();

    private final String address;
    private final Socket socket;

    private final OutputStream output;
    private final InputStream input;

    public LightifyLink(String address) {
        this.address = address;
        this.socket = exceptional(() -> new Socket(address, 4000));
        this.output = exceptional(socket::getOutputStream);
        this.input = exceptional(socket::getInputStream);
    }

    public void perform(byte command) {
        perform(command, null);
    }

    public void perform(byte command, byte[] data) {
        byte[] packet = new PacketBuilder(this).on(command).data(data).build();
        sendPacket(packet);
        readPacket(this::handlePacket, null);
    }

    private void handlePacket(ByteBuffer buffer, Consumer<LightifyLuminary> consumer) {
        System.out.println("");
        System.out.println("Header:");
        System.out.println("\tLength: " + (buffer.limit() + 2));
        System.out.println("\tPacket type: 0x" + Integer.toHexString(Byte.toUnsignedInt(buffer.get())));
        System.out.println("\tCommand type: 0x" + Integer.toHexString(Byte.toUnsignedInt(buffer.get())));
        System.out.println("\tRequest id: " + (Integer.toUnsignedString(buffer.getInt())));
        if (buffer.limit() >= 7) {
            System.out.println("\tError code: 0x" + Integer.toHexString(Byte.toUnsignedInt(buffer.get())));
        } else {
            System.out.println("!!!Short packet!!!");
        }

        if (buffer.hasRemaining()) {
            System.out.println("Data:");
            System.out.println("Size: " + buffer.remaining());
            byte[] data = new byte[buffer.remaining()];
            System.arraycopy(buffer.array(), buffer.position(), data, 0, data.length);
            System.out.println(DatatypeConverter.printHexBinary(data));
            System.out.println(Arrays.toString(data));

            //System.out.println("Status: 0x" + Integer.toHexString(Byte.toUnsignedInt(buffer.get()))); // 1

            /*int profileCount = Byte.toUnsignedInt(buffer.get()); // 2
            System.out.println("ProfileCount: " + profileCount);
            for (int i = 0; i < profileCount; i++) {
                byte[] name = new byte[32];
                buffer.get(name);
                System.out.println("Name: " + new String(name, UTF8).trim());
                byte[] ssid = new byte[33];
                buffer.get(ssid);
                System.out.println("SSID: " + new String(ssid, UTF8).trim());
                byte[] bssid = new byte[6];
                buffer.get(bssid);
                System.out.println("BSSID: " + new String(bssid, UTF8).trim());
                System.out.println("Channel: " + buffer.getInt());
                System.out.println("Unk1: " + Byte.toUnsignedInt(buffer.get()));
                System.out.println("Unk2: " + Byte.toUnsignedInt(buffer.get()));
                byte[] addr = new byte[4];
                buffer.get(addr);
                InetAddress address = exceptional(() -> Inet4Address.getByAddress(addr));
                System.out.println("IP: " + address.getHostAddress());
                buffer.get(addr);
                address = exceptional(() -> Inet4Address.getByAddress(addr));
                System.out.println("Gateway: " + address.getHostAddress());
                buffer.get(addr);
                address = exceptional(() -> Inet4Address.getByAddress(addr));
                System.out.println("Netmask: " + address.getHostAddress());
                buffer.get(addr);
                address = exceptional(() -> Inet4Address.getByAddress(addr));
                System.out.println("DNS1: " + address.getHostAddress());
                buffer.get(addr);
                address = exceptional(() -> Inet4Address.getByAddress(addr));
                System.out.println("DNS2: " + address.getHostAddress());
            }*/
        }
        System.out.println("");
        System.out.println("");
    }

    public LightifyLuminary findDevice(String address) {
        return devices.get(address);
    }

    public LightifyZone findZone(String zoneId) {
        return zones.get(zoneId);
    }

    public void performSearch(Consumer<LightifyLuminary> consumer) {
        byte[] packet = new PacketBuilder(this).on(STATUS_ALL).data(new byte[]{0x01}).build();
        sendPacket(packet);
        readPacket(this::handleStatusAll, consumer);

        packet = new PacketBuilder(this).on(ZONE_LIST).build();

        System.out.println("Searching Zones...");
        sendPacket(packet);
        readPacket(this::handleZoneList, consumer);
    }

    public void performStatusUpdate(LightifyLuminary luminary, Consumer<LightifyLuminary> consumer) {
        byte[] packet = new PacketBuilder(this).on(STATUS_SINGLE).with(luminary).build();

        sendPacket(packet);
        readPacket(this::handleStatusUpdate, consumer);
    }

    void performSwitch(LightifyLuminary lightifyLuminary, boolean activate, Consumer<LightifyLuminary> consumer) {
        byte[] packet = new PacketBuilder(this).on(LIGHT_SWITCH).with(lightifyLuminary).switching(activate).build();

        sendPacket(packet);
        readPacket(buildLightCommandHandler(LIGHT_SWITCH), light -> {
            light.updatePowered(activate);
            if (consumer != null) {
                consumer.accept(light);
            }
        });
    }

    void performLuminance(LightifyLuminary lightifyLuminary, byte luminance, short millis, Consumer<LightifyLuminary> consumer) {
        byte[] packet = new PacketBuilder(this).on(LIGHT_LUMINANCE).with(lightifyLuminary).luminance(luminance).millis(millis)
                                               .build();

        sendPacket(packet);
        readPacket(buildLightCommandHandler(LIGHT_LUMINANCE), light -> {
            light.updateLuminance(luminance);
            light.updatePowered(true);
            if (consumer != null) {
                consumer.accept(light);
            }
        });
    }

    void performRGB(LightifyLuminary lightifyLuminary, byte r, byte g, byte b, short millis,
                    Consumer<LightifyLuminary> consumer) {

        byte[] packet = new PacketBuilder(this).on(LIGHT_COLOR).with(lightifyLuminary).rgb(r, g, b).millis(millis).build();

        sendPacket(packet);
        readPacket(buildLightCommandHandler(LIGHT_COLOR), light -> {
            light.updateRGB(r, g, b);
            light.updatePowered(true);
            if (consumer != null) {
                consumer.accept(light);
            }
        });
    }

    void performTemperature(LightifyLuminary lightifyLuminary, short temperature, short millis,
                            Consumer<LightifyLuminary> consumer) {

        byte[] packet = new PacketBuilder(this).on(LIGHT_TEMPERATURE).with(lightifyLuminary).temperature(temperature)
                                               .millis(millis).build();

        sendPacket(packet);
        readPacket(buildLightCommandHandler(LIGHT_TEMPERATURE), light -> {
            light.updateTemperature(temperature);
            light.updatePowered(true);
            if (consumer != null) {
                consumer.accept(light);
            }
        });
    }

    void performZoneInfo(LightifyZone lightifyZone, Consumer<LightifyLuminary> consumer) {
        byte[] packet = new PacketBuilder(this).on(ZONE_INFO).with(lightifyZone).build();

        sendPacket(packet);
        readPacket(this::handleZoneInfo, consumer);
    }

    int nextSequence() {
        while (true) {
            int oldValue = sequencer.get();
            long next = oldValue + 1L;
            if (oldValue > Integer.MAX_VALUE) {
                next = 0;
            }
            if (sequencer.compareAndSet(oldValue, (int) next)) {
                return (int) next;
            }
        }
    }

    private BiConsumer<ByteBuffer, Consumer<LightifyLuminary>> buildLightCommandHandler(Command command) {
        return (buffer, consumer) -> {
            handleLightResponse(buffer, command, consumer);
        };
    }

    private void handleZoneInfo(ByteBuffer buffer, Consumer<LightifyLuminary> consumer) {
        // skip header
        buffer.get();
        if (ZONE_INFO.getId() != buffer.get()) {
            throw new IllegalStateException("Illegal packet type");
        }

        buffer.getShort(); // sequence number
        buffer.get(); // always 0
        buffer.get(); // always 0
        System.out.println(buffer.get()); // always 0

        int zoneId = buffer.getShort();

        byte[] nameBuffer = new byte[16];
        buffer.get(nameBuffer, 0, nameBuffer.length);
        String name = new String(nameBuffer, UTF8).trim();

        LightifyZone zone = findZone(getZoneUID(zoneId));

        int numOfDevices = buffer.get();
        for (int i = 0; i < numOfDevices; i++) {
            byte[] address = new byte[8];
            buffer.get(address, 0, address.length);
            String mac = DatatypeConverter.printHexBinary(address);

            LightifyLuminary luminary = findDevice(mac);
            zone.addDevice(luminary);
        }
        consumer.accept(zone);
    }

    private void handleZoneList(ByteBuffer buffer, Consumer<LightifyLuminary> consumer) {
        // skip header
        buffer.get();
        if (ZONE_LIST.getId() != buffer.get()) {
            throw new IllegalStateException("Illegal packet type");
        }

        buffer.getShort(); // sequence number
        buffer.get(); // always 0
        buffer.get(); // always 0
        System.out.println(buffer.get()); // always 0

        int numOfZones = buffer.getShort();
        System.out.println("Found " + numOfZones + " zones...");
        for (int i = 0; i < numOfZones; i++) {
            int zoneId = buffer.getShort();

            byte[] nameBuffer = new byte[16];
            buffer.get(nameBuffer, 0, nameBuffer.length);
            String name = new String(nameBuffer, UTF8).trim();

            LightifyZone zone = new LightifyZone(this, name, zoneId);
            zones.put(getZoneUID(zoneId), zone);
            performZoneInfo(zone, consumer);
        }
    }

    private void handleStatusUpdate(ByteBuffer buffer, Consumer<LightifyLuminary> consumer) {
        // skip header
        buffer.get();
        if (STATUS_SINGLE.getId() != buffer.get()) {
            throw new IllegalStateException("Illegal packet type");
        }

        buffer.getShort(); // sequence number
        buffer.get(); // always 0
        buffer.get(); // always 0
        System.out.println(buffer.get()); // always 0

        int id = buffer.getShort();

        byte[] address = new byte[8];
        buffer.get(address, 0, address.length);
        String mac = DatatypeConverter.printHexBinary(address);
        LightifyLuminary luminary = findDevice(mac);
        if (luminary != null) {
            buffer.getShort(); // unk1

            boolean status = buffer.get() == 1;
            byte luminance = buffer.get();
            short temperature = buffer.getShort();
            byte r = buffer.get();
            byte g = buffer.get();
            byte b = buffer.get();
            byte a = buffer.get();

            luminary.updatePowered(status);
            luminary.updateLuminance(luminance);
            luminary.updateTemperature(temperature);
            luminary.updateRGB(r, g, b);

            consumer.accept(luminary);
        }
    }

    private void handleLightResponse(ByteBuffer buffer, Command command, Consumer<? super LightifyLuminary> consumer) {
        // skip header
        buffer.get();
        if (command.getId() != buffer.get()) {
            throw new IllegalStateException("Illegal packet type");
        }

        buffer.getShort(); // sequence number
        buffer.get(); // always 0
        buffer.get(); // always 0
        System.out.println(buffer.get()); // always 0

        int id = buffer.getShort();

        byte[] address = new byte[8];
        buffer.get(address, 0, address.length);
        String mac = DatatypeConverter.printHexBinary(address);
        LightifyLuminary luminary = findDevice(mac);
        if (luminary != null) {
            consumer.accept(luminary);
        }
    }

    private void handleStatusAll(ByteBuffer buffer, Consumer<? super LightifyLuminary> consumer) {
        // skip header
        buffer.get();
        if (STATUS_ALL.getId() != buffer.get()) {
            throw new IllegalStateException("Illegal packet type");
        }

        buffer.getShort(); // sequence number
        buffer.get(); // always 0
        buffer.get(); // always 0
        buffer.get(); // always 0

        int numOfLights = buffer.getShort();
        System.out.println("Found " + numOfLights + " devices...");
        for (int i = 0; i < numOfLights; i++) {
            int id = buffer.getShort();

            // Address (8)
            byte[] address = new byte[8];
            buffer.get(address, 0, address.length);

            // Information (8)
            byte type = buffer.get();
            String firmwareX = String.format("%02d", buffer.get());
            firmwareX += String.format("%02d", buffer.get());
            firmwareX += String.format("%02d", buffer.get());
            firmwareX += buffer.get();
            firmwareX += buffer.get();

            short groupId = buffer.getShort();

            // Stats (8)
            boolean status = buffer.get() == 1;
            byte luminance = buffer.get();
            short temperature = buffer.getShort();
            byte r = buffer.get();
            byte g = buffer.get();
            byte b = buffer.get();
            byte a = buffer.get(); // alpha (seems to be always 0xff)
            // Stats

            // Name (24)
            byte[] nameBuffer = new byte[24];
            buffer.get(nameBuffer, 0, nameBuffer.length);
            String name = new String(nameBuffer, UTF8).trim();

            LightifyLight light = new LightifyLight(this, name, address);

            // Push values
            light.updateLuminance(luminance);
            light.updateTemperature(temperature);
            light.updateRGB(r, g, b);
            light.updatePowered(status);

            String mac = DatatypeConverter.printHexBinary(light.address());
            devices.put(mac, light);

            consumer.accept(light);
        }
    }

    private void sendPacket(byte[] packet) {
        exceptional(() -> {
            output.write(packet);
            output.flush();
        });
    }

    private void readPacket(BiConsumer<ByteBuffer, Consumer<LightifyLuminary>> handler, Consumer<LightifyLuminary> consumer) {
        ByteBuffer buffer = exceptional(() -> {
            int length = 2;
            ByteBuffer b = waitForBytes(length, ByteOrder.LITTLE_ENDIAN);

            length = b.getShort();
            return waitForBytes(length, ByteOrder.LITTLE_ENDIAN);
        });

        handler.accept(buffer, consumer);
    }

    private ByteBuffer waitForBytes(int length, ByteOrder byteOrder)
            throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(length).order(byteOrder);
        int read = 0;
        while (read != length) {
            read += input.read(buffer.array(), read, length - read);
        }
        return buffer;
    }

    public void disconnect() {
        exceptional(socket::close);
    }

    private String getZoneUID(int zoneId) {
        return "zone::" + zoneId;
    }
}
