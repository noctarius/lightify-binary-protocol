package com.noctarius.lightify;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketAnalyzer {

    public static void main(String[] args) {
        byte[] data = DatatypeConverter.parseHexBinary("070000133928e55b01");
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        System.out.println("Packet length (WORD): " + Short.toUnsignedInt(buffer.getShort()));
        System.out.println("Packet type (BYTE): 0x" + Integer.toHexString(buffer.get()));
        System.out.println("Command id (BYTE): 0x" + Integer.toHexString(buffer.get()));
        System.out.println("Request id (WORD): " + Integer.toHexString(getWord(buffer)));
        System.out.println("Unknown (WORD): 0x" + Integer.toHexString(getWord(buffer)));
        System.out.println("Error code (BYTE): 0x" + Integer.toHexString(buffer.get()));

        System.out.println("More data available: " + (buffer.remaining() > 0));
    }

    private static int getWord(ByteBuffer buffer) {
        return Short.toUnsignedInt(buffer.getShort());
    }
}
