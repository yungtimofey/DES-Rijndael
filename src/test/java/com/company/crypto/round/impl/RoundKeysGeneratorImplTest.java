package com.company.crypto.round.impl;

import com.company.crypto.round.RoundKeysGenerator;
import org.junit.jupiter.api.Test;


import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

class RoundKeysGeneratorImplTest {
//    @Test
//    public void firstTest() {
//        RoundKeysGenerator roundKeysGenerator = new RoundKeysGeneratorDES();
//        byte[][] array;
//
//        BitSet bitSet = init(64, 25, 26, 30, 33, 34, 37, 41, 42, 49, 50, 53, 54, 56, 57, 58);
//        byte[] key = bitSet.toByteArray();
//        array = roundKeysGenerator.generate(key);
//
//        byte[] check = init(64, 3, 5, 7, 8, 12, 15, 16, 18, 24, 28, 29, 32, 34).toByteArray();
//        assertArrayEquals(array[0], check);
//
//        check = init(64, 3, 5, 7, 8, 12, 18, 20, 21, 24, 37, 39, 43, 46).toByteArray();
//        assertArrayEquals(array[1], check);
//
//    }

    private BitSet init(int size, int ... indexes) {
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