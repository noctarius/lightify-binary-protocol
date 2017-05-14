package com.noctarius.lightify.protocol;

import javax.jmdns.ServiceInfo;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

public final class LightifyUtils {

    private LightifyUtils() {
    }

    public static String readDeviceFirmware(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder(String.format("%02d", Byte.toUnsignedInt(buffer.get())));
        sb.append(String.format("%02d", Byte.toUnsignedInt(buffer.get())));
        sb.append(String.format("%02d", Byte.toUnsignedInt(buffer.get())));
        sb.append(Byte.toUnsignedInt(buffer.get()));
        return sb.append(Byte.toUnsignedInt(buffer.get())).toString();
    }

    public static String toHexMac(byte[] address) {
        StringBuilder sb = new StringBuilder(Integer.toHexString(Byte.toUnsignedInt(address[0])).toUpperCase()).append(":");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(address[1])).toUpperCase()).append(":");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(address[2])).toUpperCase()).append(":");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(address[3])).toUpperCase()).append(":");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(address[4])).toUpperCase()).append(":");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(address[5])).toUpperCase()).append(":");
        sb.append(Integer.toHexString(Byte.toUnsignedInt(address[6])).toUpperCase()).append(":");
        return sb.append(Integer.toHexString(Byte.toUnsignedInt(address[7])).toUpperCase()).toString();
    }

    public static boolean isLightifyGateway(ServiceInfo serviceInfo) {
        return serviceInfo.getName().contains("Lightify-");
    }

    public static String extractLightifyUID(String name) {
        return name.replace("Lightify-", "");
    }

    public static <R> R exceptional(Callable<R> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void exceptional(Exceptional exceptional) {
        try {
            exceptional.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface Exceptional {
        void call() throws Exception;
    }
}
