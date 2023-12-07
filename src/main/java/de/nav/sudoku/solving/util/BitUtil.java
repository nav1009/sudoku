package de.nav.sudoku.solving.util;

public class BitUtil {

    static public int singleBit(int bit) {
        return (0b1 << (bit - 1));
    }

    static public boolean isPowerOfTwo(int number) {
        return (number != 0) && (number & (number - 1)) == 0;
    }

    static public boolean valueHasBitSet(int value, int bit) {
        return ((value >> bit) & 1) == 1;
    }

    static public int setBitOfValue(int value, int bit) {
        return (value | (1 << bit));
    }

    static public boolean bitCountEquals(int value, int bitCount) {
        return Integer.bitCount(value) == bitCount;
    }
}