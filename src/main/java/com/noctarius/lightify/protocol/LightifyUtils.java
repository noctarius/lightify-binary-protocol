package com.noctarius.lightify.protocol;

import javax.jmdns.ServiceInfo;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

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
        return DatatypeConverter.printHexBinary(address);
    }

    public static boolean isLightifyGateway(ServiceInfo serviceInfo) {
        return serviceInfo.getName().contains("Lightify-");
    }

    public static String extractLightifyUID(String name) {
        return name.replace("Lightify-", "");
    }

    public static <R> R exceptional(Callable<R> callable) {
        return exceptional(callable, (e) -> {
            throw new RuntimeException(e);
        });
    }

    public static <R> R exceptional(Callable<R> callable, Consumer<Exception> exceptionConsumer) {
        try {
            return callable.call();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
            throw new RuntimeException(e); // if the consumer does not throw an exception, we'll do it here
        }
    }

    public static void exceptional(Exceptional exceptional) {
        exceptional(exceptional, (e) -> {
            throw new RuntimeException(e);
        });
    }

    public static void exceptional(Exceptional exceptional, Consumer<Exception> exceptionConsumer) {
        try {
            exceptional.call();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
        }
    }

    public interface Exceptional {
        void call()
                throws Exception;
    }
}
