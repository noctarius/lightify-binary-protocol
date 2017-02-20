package com.noctarius.lightify.model;

import com.noctarius.lightify.protocol.Address;

public abstract class LightifyLuminary extends LightifyDevice implements Switchable {

    private boolean on;

    protected LightifyLuminary(boolean on, Address address) {
        super(address);
        this.on = on;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
