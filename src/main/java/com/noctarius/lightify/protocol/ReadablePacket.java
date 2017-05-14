package com.noctarius.lightify.protocol;

import java.util.function.Consumer;

public interface ReadablePacket extends Packet {

    void accept(Consumer<? super ReadablePacket> handler);
}
