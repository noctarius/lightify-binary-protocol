package com.noctarius.lightify;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Test {

    private static final Charset UTF8 = Charset.forName("UTF8");

    public static void main(String[] args) {
        /*if(true) {
            System.out.println(Integer.toHexString(Byte.toUnsignedInt((byte) -35)));
            return;
        }*/

        //for (int i = 0xd4; i < 0xe0; i++) {
        int i = 0xd4;
            try {                                                 //"172.25.100.141"  192.168.10.1
                LightifyLink link = new LightifyLink("172.25.100.141");

                /*ByteBuffer buffer = ByteBuffer.allocate(125).order(ByteOrder.LITTLE_ENDIAN);
                buffer.put((byte) 1); // set wifi
                putString(buffer, "ISD", 33);
                putString(buffer, "VelkynVeldrin1404", 65);
                buffer.putInt(0);
                buffer.put((byte) 0);
                buffer.put((byte) 1);
                buffer.put(ByteBuffer.allocate(20).array());*/

                link.perform((byte) i, new byte[] {(byte) 0});
                link.disconnect();
            } catch (Exception e) {
                System.out.println("0x" + Integer.toHexString(i) + ": " + e.getMessage());
            }
        //}
    }

    private static void putString(ByteBuffer buffer, String value, int length) {
        ByteBuffer b = ByteBuffer.allocate(length);
        b.put(value.getBytes(UTF8));
        b.position(b.limit());
        b.rewind();
        buffer.put(b);
    }

}
