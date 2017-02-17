package com.noctarius.lightify;

import java.util.Arrays;

public class LightifyLight extends LightifyLuminary {

    private final byte[] address;

    LightifyLight(LightifyLink lightifyLink, String name, byte[] address) {
        super(lightifyLink, name);
        this.address = address;
    }

    @Override
    public byte[] address() {
        return address;
    }

    @Override
    public String toString() {
        return "com.noctarius.lightify.LightifyLight{" + "address=" + Arrays.toString(address) + ", super=" + super.toString() + '}';
    }

    @Override
    byte typeFlag() {
        return 0x00;
    }
}
