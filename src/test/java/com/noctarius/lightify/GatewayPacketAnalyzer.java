package com.noctarius.lightify;

import com.noctarius.lightify.protocol.Command;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GatewayPacketAnalyzer {

    public static void main(String[] args) {
        byte[] data = DatatypeConverter.parseHexBinary("002c000001960000000000010000000c02030320000c00000000000000000000080a0000010000000100000007");
        ByteBuffer buffer = ByteBuffer.wrap(data);

        System.out.println("Packet length (WORD): " + getWord(buffer));

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        System.out.println("Packet type (BYTE): 0x" + Integer.toHexString(buffer.get()));
        int commandId = Byte.toUnsignedInt(buffer.get());
        System.out.println("Command id (BYTE): 0x" + Integer.toHexString(commandId));
        System.out.println("Command: " + Command.byId(commandId));
        System.out.println("Request id (DWORD): 0x" + Integer.toHexString(buffer.getInt()));
        System.out.println("Error code (BYTE): 0x" + Integer.toHexString(buffer.get()));

        System.out.println("More data available: " + (buffer.remaining() > 0));

        byte[] additionalData = new byte[buffer.remaining()];
        System.arraycopy(buffer.array(), buffer.position(), additionalData, 0, buffer.remaining());
        System.out.println(DatatypeConverter.printHexBinary(additionalData));
    }

    private static int getWord(ByteBuffer buffer) {
        return Short.toUnsignedInt(buffer.getShort());
    }
}
