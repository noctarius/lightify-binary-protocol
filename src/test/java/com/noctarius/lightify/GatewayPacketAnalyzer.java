package com.noctarius.lightify;

import com.noctarius.lightify.protocol.Command;
import com.noctarius.lightify.protocol.packets.GetDeviceInfoResponse;
import com.noctarius.lightify.protocol.packets.PacketFactory;
import com.noctarius.lightify.protocol.packets.SetLuminanceResponse;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GatewayPacketAnalyzer {

    public static void main(String[] args) throws Exception {
        byte[] data = DatatypeConverter.parseHexBinary("1200013126000000000100A219A100AA3EB07C01");
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        Constructor<SetLuminanceResponse> constructor = SetLuminanceResponse.class.getDeclaredConstructor(ByteBuffer.class);
        constructor.setAccessible(true);
        SetLuminanceResponse response = constructor.newInstance(buffer);
        System.out.println(response);

        buffer.position(0);
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
