package com.noctarius.lightify;

import com.noctarius.lightify.model.Switchable;

public class Test2 {

    public static void main(String[] args)
            throws Exception {
        LightifyLink link = new LightifyLink("172.25.100.141", null);
        link.performSearch((device) -> {
            if (device instanceof Switchable) {
                Switchable switchable = (Switchable) device;
                link.performSwitch(switchable, false, System.out::println);
            }
        });
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }
}
