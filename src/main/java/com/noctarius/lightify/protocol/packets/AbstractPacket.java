package com.noctarius.lightify.protocol.packets;

import com.noctarius.lightify.Command;
import com.noctarius.lightify.LightifyLink;
import com.noctarius.lightify.protocol.Status;

import java.nio.ByteBuffer;

public abstract class AbstractLightifyPacket {

    private final short length;
    private final byte flag;
    private final Command command;
    private final int requestId;
    private final Status status;

    protected AbstractLightifyPacket(byte flag, Command command) {
        this.flag = flag;
        this.command = command;
    }

    public byte getFlag() {
        return flag;
    }

    public Command getCommand() {
        return command;
    }

    public final ByteBuffer build(LightifyLink lightifyLink) {
        return null; // TODO
    }

    protected abstract void buildPayload(ByteBuffer byteBuffer);
}
