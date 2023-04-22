package com.company.crypto.round.impl;

import com.company.crypto.round.RoundKeysGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

class RoundTransformerImplTest {
    static byte[][] arrayKey;
    static byte[] input64Bit;
    static BitSet input, leftAnswer, rightAnswer;
    static RoundTransformerDES roundTransformer = new RoundTransformerDES();

    @BeforeAll
    static void generateKey() {
        RoundKeysGenerator roundKeysGenerator = new RoundKeysGeneratorDES();
        BitSet bitSet = init(64, 25, 26, 30, 33, 34, 37, 41, 42, 49, 50, 53, 54, 56, 57, 58);
        byte[] key = bitSet.toByteArray();
        arrayKey = roundKeysGenerator.generate(key);
    }

    @BeforeAll
    static void init() {
        input = init(64, 1, 2, 3, 4, 5, 6, 10, 11, 18, 20, 22, 26,
                33, 34, 35, 36, 37, 38, 49, 50, 52, 57, 58, 60, 61);

        input64Bit = input.toByteArray();
        System.out.print("Input:");
        print(input);

        System.out.println(Long.toBinaryString(input.toLongArray()[0]));

        leftAnswer = init(32, 1, 2, 3, 4, 5, 6, 10, 11, 18, 20, 22, 26);
        System.out.print("Left answer:");
        print(leftAnswer);

        rightAnswer = init(32, 1, 2, 3, 4, 5, 6, 17, 18, 20, 25, 26, 28, 29);
        System.out.print("Right answer:");
        print(rightAnswer);
    }

    @Test
    void leftHalfTest() {
        BitSet left = roundTransformer.getLeftHalf(input);
        assertArrayEquals(left.toByteArray(), leftAnswer.toByteArray());
    }

    @Test
    void rightHalfTest() {
        BitSet right = roundTransformer.getRightHalf(input);
        assertArrayEquals(right.toByteArray(), rightAnswer.toByteArray());
    }

    @Test
    void expandRightHalf() {
        BitSet right = roundTransformer.getRightHalf(input);
        right = roundTransformer.expandHalf(right);

        BitSet answer = init(48, 2, 3, 4, 5, 6, 7, 8, 9, 24, 26, 27, 29, 31, 36, 38, 39, 41, 42, 43, 44, 48);
        assertArrayEquals(right.toByteArray(), answer.toByteArray());
    }

//    @Test
//    void testXoredExpandedHalf() {
//        BitSet right = roundTransformer.getRightHalf(input);
//        right = roundTransformer.expandHalf(right);
//        right.xor(BitSet.valueOf(arrayKey[0]));
//
//        BitSet answer = init(32, 2, 4, 6, 9, 12, 15, 16, 18, 26, 27, 28, 31, 32, 34, 36,
//                38, 39, 41, 42, 43, 44, 48);
//
//        assertArrayEquals(right.toByteArray(), answer.toByteArray());
//    }

//    @Test
//    void reduceXoredRightHalf() {
//        BitSet right = roundTransformer.getRightHalf(input);
//        right = roundTransformer.expandHalf(right);
//
//        right.xor(BitSet.valueOf(arrayKey[0]));
//
//        BitSet xored = roundTransformer.sPermutation(right, 6, 4);
//
//        BitSet answer = init(32, 1, 2, 5, 6, 7, 8, 10, 11, 14, 15, 16, 17, 18, 19, 24, 25, 26, 27, 28, 29, 30, 31, 32);
//        assertArrayEquals(xored.toByteArray(), answer.toByteArray());
//    }

//    @Test
//    void lastPermutation() {
//        BitSet right = roundTransformer.getRightHalf(input);
//        right = roundTransformer.expandHalf(right);
//
//        right.xor(BitSet.valueOf(arrayKey[0]));
//
//        BitSet xored = roundTransformer.sPermutation(right, 6, 4);
//        BitSet last = roundTransformer.lastPermutation(xored);
//
//        BitSet ans = init(32, 1, 2, 5, 7, 8, 9, 10, 12, 13, 14, 15, 16,
//                17, 18, 19, 20, 21, 22, 25, 27, 28, 30, 32);
//
//        assertArrayEquals(last.toByteArray(), ans.toByteArray());
//    }

    @Test
    void combineTest() {
        BitSet right = roundTransformer.getRightHalf(input);
        right = roundTransformer.expandHalf(right);

        right.xor(BitSet.valueOf(arrayKey[0]));

        BitSet xored = roundTransformer.sPermutation(right, 6, 4);
        BitSet left = init(32, 1, 2, 3, 4, 5, 6, 10, 11, 18, 20, 22, 26);

        BitSet combined = roundTransformer.combineTwoParts(left, xored);

//        BitSet answer = init(64, )
//        assertArrayEquals(combined.toByteArray(), );
    }

//    @Test
//    void allCheck() {
//        byte[] ans = roundTransformer.doRound(input.toByteArray(), arrayKey[0], false);
//
//        BitSet trueAns = init(64, 3 + 32, 4 + 32, 6 + 32, 7 + 32, 8 + 32,
//                9 + 32, 11 + 32, 12 + 32, 13 + 32, 14 + 32, 15 + 32, 16 + 32,
//                17 + 32, 19 + 32, 21 + 32, 25 + 32, 26 + 32, 27 + 32, 28  + 32, 30 + 32, 32 + 32,
//                1, 2, 3, 4, 5, 6, 17, 18, 20, 25, 26, 28, 29);
//        print(trueAns);
//
//        assertArrayEquals(ans, trueAns.toByteArray());
//    }

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
}