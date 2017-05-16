package com.noctarius.lightify;

public class Test2 {

    public static void main(String[] args)
            throws Exception {
        LightifyLink link = new LightifyLink("172.25.100.141", null);
        link.performSearch(System.out::println);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }
}
