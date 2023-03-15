package com.company.crypto.algorithm.impl;

import com.company.crypto.round.impl.RoundKeysGeneratorImpl;
import com.company.crypto.round.impl.RoundTransformerImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class DESTest {
    static DES des;
    static byte[] key;
    static byte[] input64Bit;

    @BeforeAll
    public static void init() {
        des = new DES(new RoundKeysGeneratorImpl(), new RoundTransformerImpl());

        BitSet bitSet = init(64, 25, 26, 30, 33, 34, 37, 41, 42, 49, 50, 53, 54, 56, 57, 58);
        key = bitSet.toByteArray();

        BitSet input = init(64, 17, 18, 22, 25, 26, 31, 33, 34, 37, 38, 39, 41, 42, 44,
        49, 50, 52, 53, 54, 55, 56, 57, 58, 61, 63);

        input64Bit = input.toByteArray();

        des.setKey64Bit(key);
    }

//    @Test
//    void checkFirstEncodeWithoutReverseIP() {
//        byte[] encoded = des.encode(input64Bit);
//        print(BitSet.valueOf(encoded));
//
//        // without reverseIP!!! one round! Not last round!!!
//        BitSet trueAns = init(64, 3 + 32, 4 + 32, 6 + 32, 7 + 32, 8 + 32,
//                9 + 32, 11 + 32, 12 + 32, 13 + 32, 14 + 32, 15 + 32, 16 + 32,
//                17 + 32, 19 + 32, 21 + 32, 25 + 32, 26 + 32, 27 + 32, 28  + 32, 30 + 32, 32 + 32,
//                1, 2, 3, 4, 5, 6, 17, 18, 20, 25, 26, 28, 29);
//
//        assertArrayEquals(encoded, trueAns.toByteArray());
//    }

//    @Test
//    void checkReversePermutations() {
//        byte[] inputBlock64Bit = des.encode(input64Bit);
//        byte[] encodedAfterIP = des.IP(BitSet.valueOf(inputBlock64Bit));
//
//        print(BitSet.valueOf(inputBlock64Bit));
//        print(BitSet.valueOf(encodedAfterIP));
//
//        assertArrayEquals(inputBlock64Bit, encodedAfterIP);
//    }

    @Test
    void checkEncodeAndDecode() {
        byte[] encoded = des.encode(input64Bit);
        print(BitSet.valueOf(encoded));

        byte[] decoded = des.decode(encoded);
        print(BitSet.valueOf(decoded));

        assertArrayEquals(input64Bit, decoded);
    }

    private static void print(BitSet bitSet) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bitSet.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                s.append(' ');
            }
            s.append(bitSet.get(i) ? 1 : 0);
        }
        System.out.println(s);
    }



    private static BitSet init(int size, int ... indexes) {
        BitSet bitSet = new BitSet(size);
        for (int i = 0; i < size; i++) {
            bitSet.set(i, false);
        }

        for (int index : indexes) {
            bitSet.set(index-1, true);
        }

        return bitSet;
    }
}