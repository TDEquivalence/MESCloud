package com.alcegory.mescloud.utility;

public class DoubleUtil {

    public static Double safeDoubleValue(Integer integer) {
        return integer != null ? Double.valueOf(integer) : null;
    }

    public static Double safeDoubleValue(Long integer) {
        return integer != null ? Double.valueOf(integer) : null;
    }
}
