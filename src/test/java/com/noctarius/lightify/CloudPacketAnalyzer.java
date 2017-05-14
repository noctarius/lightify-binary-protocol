package com.noctarius.lightify;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CloudPacketAnalyzer {

    public static void main(String[] args) {
        byte[] data = DatatypeConverter.parseHexBinary("002c000001960000000000010000000c02030320000c00000000000000000000080a0000010000000100000007");
        ByteBuffer buffer = ByteBuffer.wrap(data);

        System.out.println("Packet length: " + getWord(buffer));
        
    }

    public static int getWord(ByteBuffer buffer) {
        return Short.toUnsignedInt(buffer.getShort());
    }

    public static long getDword(ByteBuffer buffer) {
        return Integer.toUnsignedLong(buffer.getInt());
    }

    public static byte[] getQword(ByteBuffer buffer) {
        byte[] data = new byte[8];
        buffer.get(data);
        return data;
    }
}
