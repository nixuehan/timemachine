package me.nixuehan.tcc.utils;

public class Assert {

    public static void isNull(Object o) {
        if (o == null || o == "") {
            throw new IllegalStateException();
        }
    }
}
