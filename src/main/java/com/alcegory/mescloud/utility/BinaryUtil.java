package com.alcegory.mescloud.utility;

import lombok.extern.java.Log;

@Log
public class BinaryUtil {

    private BinaryUtil() {
        //Utility class, not meant for instantiation
    }

    public static boolean[][] toBinaryUnsigned(int[] integers, int bitsPerInteger) throws IllegalArgumentException {

        boolean[][] alarmBits = new boolean[integers.length][bitsPerInteger];
        for (int i = 0; i < integers.length; i++) {

            if (integers[i] < 0) {
                String message = String.format("Unable to convert to binary representation: positive integers expected and received [%s]", integers[i]);
                log.warning(message);
                throw new IllegalArgumentException(message);
            }

            alarmBits[i] = toBinaryUnsigned(integers[i], bitsPerInteger);
        }

        return alarmBits;
    }

    public static boolean[] toBinaryUnsigned(int integer, int integerBitSize) {

        boolean[] bitArray = new boolean[integerBitSize];
        for (int bitIndex = 0; bitIndex < integerBitSize; bitIndex++) {

            int mask = 1 << (integerBitSize - 1 - bitIndex);
            bitArray[bitIndex] = (integer & mask) != 0;
        }

        return bitArray;
    }
}