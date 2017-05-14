package com.noctarius.lightify.model;

public interface ColorLight
        extends Addressable {

    short getRed();

    short getGreen();

    short getBlue();

    void setRed(short red);

    void setGreen(short green);

    void setBlue(short blue);
}
