package com.alcegory.mescloud.util;

import com.alcegory.mescloud.utility.BinaryUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryUtilTest {

    @Test
    void toBinaryUnsigned_positiveIntegers() {
        int[] positiveIntegers = {5, 10, 15, 255};
        int bitsPerInteger = 8;

        boolean[][] expectedBits = {
                {false, false, false, false, false, true, false, true}, //5 in binary (8 bits): 0000 0101
                {false, false, false, false, true, false, true, false}, //10 in binary (8 bits): 0000 1010
                {false, false, false, false, true, true, true, true}, //15 in binary (8 bits): 0000 1111
                {true, true, true, true, true, true, true, true} //255 in binary (8 bits): 1111 1111
        };
        boolean[][] actualBits = BinaryUtil.toBinaryUnsigned(positiveIntegers, bitsPerInteger);

        assertArrayEquals(expectedBits, actualBits);
    }

    @Test
    void toBinaryUnsigned_zero_false() {
        int[] zeroInteger = {0};
        int bitsPerInteger = 1;

        boolean[][] expectedBits = {{false}};
        boolean[][] actualBits = BinaryUtil.toBinaryUnsigned(zeroInteger, bitsPerInteger);

        assertArrayEquals(expectedBits, actualBits);
    }

    @Test
    void toBinaryUnsigned_negativeInteger_illegalArgumentException() {
        int[] negativeIntegers = {-5};
        int bitsPerInteger = 8;

        assertThrows(IllegalArgumentException.class, () -> BinaryUtil.toBinaryUnsigned(negativeIntegers, bitsPerInteger));
    }

    @Test
    void toBinaryUnsigned_singleInteger() {
        int integer = 12;
        int integerBitSize = 4;

        boolean[] expectedBits = {true, true, false, false}; //12 in binary (4 bits): 1100
        boolean[] actualBits = BinaryUtil.toBinaryUnsigned(integer, integerBitSize);

        assertArrayEquals(expectedBits, actualBits);
    }
}
