package com.noctarius.lightify.model;

public interface DimmableLight
        extends Addressable {

    short getLuminance();

    void setLuminance(short luminance);
}
