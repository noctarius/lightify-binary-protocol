package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.Address;
import com.noctarius.lightify.protocol.Packet;
import com.noctarius.lightify.protocol.Status;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static com.noctarius.lightify.protocol.LightifyUtils.readDeviceFirmware;

public abstract class AbstractPacket implements Packet {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final int packetLength;
    private final short flag;
    private final Command command;
    private final long requestId;
    private final Status status;

    protected AbstractPacket(int packetLength, short flag, Command command, long requestId) {
        this.packetLength = packetLength + 6;
        this.flag = flag;
        this.command = command;
        this.requestId = requestId;
        this.status = Status.NoError;
    }

    protected AbstractPacket(ByteBuffer buffer) {
        packetLength = Short.toUnsignedInt(buffer.getShort());
        flag = (short) Byte.toUnsignedInt(buffer.get());
        command = Command.byId(buffer.get());
        requestId = Integer.toUnsignedLong(buffer.getInt());
        status = Status.byCode(buffer.get());
    }

    @Override
    public int getPacketLength() {
        return packetLength;
    }

    @Override
    public short getFlag() {
        return flag;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public long getRequestId() {
        return requestId;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void write(ByteBuffer buffer) {
        writeHeader(buffer);
        writeAddress(buffer);
        writePayload(buffer);
    }

    private void writeHeader(ByteBuffer buffer) {
        buffer.putShort((short) (getPacketLength()));
        buffer.put((byte) flag);
        buffer.put(command.getId());
        buffer.putInt((int) requestId);
    }

    protected void writeAddress(ByteBuffer buffer) {
    }

    protected void writePayload(ByteBuffer buffer) {
    }

    static String readString(int length, ByteBuffer buffer) {
        byte[] data = new byte[length];
        buffer.get(data);
        return new String(data, UTF8).trim();
    }

    static void writeString(String value, int length, ByteBuffer buffer) {
        ByteBuffer data = ByteBuffer.allocate(length).put(value.getBytes(UTF8));
        data.rewind();
        buffer.put(data);
    }

    static Address readAddress(ByteBuffer buffer) {
        byte[] address = new byte[8];
        buffer.get(address);
        return Address.fromMac(address);
    }

    static void writeAddress(Address address, ByteBuffer buffer) {
        buffer.put(address.getAddress());
    }

    static Zone readZone(ByteBuffer buffer) {
        int zoneId = Short.toUnsignedInt(buffer.getShort());
        String name = readString(16, buffer);
        return new Zone(zoneId, name);
    }

    static Device readDevice(ByteBuffer buffer) {
        int deviceId = Short.toUnsignedInt(buffer.getShort());
        Address address = readAddress(buffer);
        byte type = buffer.get();
        String firmware = readDeviceFirmware(buffer);
        int zoneId = Short.toUnsignedInt(buffer.getShort());
        boolean on = buffer.get() != 0;
        short luminance = (short) Byte.toUnsignedInt(buffer.get());
        int temperature = Short.toUnsignedInt(buffer.getShort());
        short red = (short) Byte.toUnsignedInt(buffer.get());
        short green = (short) Byte.toUnsignedInt(buffer.get());
        short blue = (short) Byte.toUnsignedInt(buffer.get());
        short alpha = (short) Byte.toUnsignedInt(buffer.get());
        String name = readString(24, buffer);
        return new Device(deviceId, address, type, firmware, zoneId, on, luminance, temperature, red, green, blue, alpha, name);
    }

    public static final class Device {
        private final int deviceId;
        private final Address address;
        private final byte type;
        private final String firmware;
        private final int zoneId;
        private final boolean on;
        private final short luminance;
        private final int temperature;
        private final short red;
        private final short green;
        private final short blue;
        private final short alpha;
        private final String name;

        private Device(int deviceId, Address address, byte type, String firmware, int zoneId, boolean on, short luminance,
                      int temperature, short red, short green, short blue, short alpha, String name) {

            this.deviceId = deviceId;
            this.address = address;
            this.type = type;
            this.firmware = firmware;
            this.zoneId = zoneId;
            this.on = on;
            this.luminance = luminance;
            this.temperature = temperature;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.name = name;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public Address getAddress() {
            return address;
        }

        public byte getType() {
            return type;
        }

        public String getFirmware() {
            return firmware;
        }

        public int getZoneId() {
            return zoneId;
        }

        public boolean isOn() {
            return on;
        }

        public short getLuminance() {
            return luminance;
        }

        public int getTemperature() {
            return temperature;
        }

        public short getRed() {
            return red;
        }

        public short getGreen() {
            return green;
        }

        public short getBlue() {
            return blue;
        }

        public short getAlpha() {
            return alpha;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Device)) {
                return false;
            }

            Device device = (Device) o;

            if (deviceId != device.deviceId) {
                return false;
            }
            if (type != device.type) {
                return false;
            }
            if (zoneId != device.zoneId) {
                return false;
            }
            if (on != device.on) {
                return false;
            }
            if (luminance != device.luminance) {
                return false;
            }
            if (temperature != device.temperature) {
                return false;
            }
            if (red != device.red) {
                return false;
            }
            if (green != device.green) {
                return false;
            }
            if (blue != device.blue) {
                return false;
            }
            if (alpha != device.alpha) {
                return false;
            }
            if (address != null ? !address.equals(device.address) : device.address != null) {
                return false;
            }
            if (firmware != null ? !firmware.equals(device.firmware) : device.firmware != null) {
                return false;
            }
            return name != null ? name.equals(device.name) : device.name == null;
        }

        @Override
        public int hashCode() {
            int result = deviceId;
            result = 31 * result + (address != null ? address.hashCode() : 0);
            result = 31 * result + (int) type;
            result = 31 * result + (firmware != null ? firmware.hashCode() : 0);
            result = 31 * result + zoneId;
            result = 31 * result + (on ? 1 : 0);
            result = 31 * result + (int) luminance;
            result = 31 * result + temperature;
            result = 31 * result + (int) red;
            result = 31 * result + (int) green;
            result = 31 * result + (int) blue;
            result = 31 * result + (int) alpha;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Device{" + "deviceId=" + deviceId + ", address=" + address + ", type=" + type + ", firmware='" + firmware
                    + '\'' + ", zoneId=" + zoneId + ", on=" + on + ", luminance=" + luminance + ", temperature=" + temperature
                    + ", red=" + red + ", green=" + green + ", blue=" + blue + ", alpha=" + alpha + ", name='" + name + '\''
                    + '}';
        }
    }

    public static final class Zone {
        private final int zoneId;
        private final String name;

        private Zone(int zoneId, String name) {
            this.zoneId = zoneId;
            this.name = name;
        }

        public int getZoneId() {
            return zoneId;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Zone)) {
                return false;
            }

            Zone zone = (Zone) o;

            if (zoneId != zone.zoneId) {
                return false;
            }
            return name != null ? name.equals(zone.name) : zone.name == null;
        }

        @Override
        public int hashCode() {
            int result = zoneId;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
