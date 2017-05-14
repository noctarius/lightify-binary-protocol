package com.noctarius.lightify.model;

public interface Switchable
        extends Addressable {

    boolean isOn();

    void setOn(boolean on);
}
