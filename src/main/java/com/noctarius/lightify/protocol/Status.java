package com.noctarius.lightify.protocol;

public enum Status {
    /**
     * ??? No Error
     */
    NoError(0x0),

    /**
     * ??? Wrong number of parameters or wrong value?
     */
    WrongNumberOfParameters(0x1),

    /**
     * ??? Unknown 1
     */
    Unk1(0x14),

    /**
     * Wrong addressing
     */
    WrongAddressing(0x15),

    /**
     * ??? Unknown 2
     */
    Unk2(0x16),

    /**
     * ??? Unknown 3
     */
    Unk3(0xA7),

    /**
     * ??? Unknown 4
     */
    Unk4(0x0B),

    /**
     * ??? Unknown 5
     */
    Unk5(0xC2),

    /**
     * ??? Unknown 6
     */
    Unk6(0xD1),

    /**
     * ??? Unknown command
     */
    UnknownCommand(0xFF);

    private static final Status[] VALUES = values();

    private final byte code;

    Status(int code) {
        this.code = (byte) code;
    }

    public byte code() {
        return code;
    }

    public static Status byCode(byte code) {
        for (Status status : VALUES) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status value: 0x" + Integer.toHexString(Byte.toUnsignedInt(code)));
    }

    public static Status byCode(int code) {
        return byCode((byte) code);
    }
}
