package com.alcegory.mescloud.utility;

public class DoubleUtil {

    private DoubleUtil() {
        //Utility class, not meant for instantiation
    }

    public static Double safeDoubleValue(Integer integer) {
        return integer != null ? Double.valueOf(integer) : null;
    }

    public static Double safeDoubleValue(Long integer) {
        return integer != null ? Double.valueOf(integer) : null;
    }
}
