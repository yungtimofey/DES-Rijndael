package com.company.crypto.round.impl;

import com.company.crypto.round.RoundKeysGenerator;

import java.util.BitSet;

public final class RoundKeysGeneratorDES implements RoundKeysGenerator {
    private static final int OUTPUT_KEY_LENGTH = 48;
    private static final int PC_1_LENGTH = 56;
    private static final int ROUND_NUMBER = 16;
    private static final int BITS_IN_BYTE = 8;
    private static final int[] PC_1 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };
    private static final int[] PC_2 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };
    private static final int[] bitsRotation = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    @Override
    public byte[][] generate(byte[] key) {
        byte[][] roundKeys = new byte[ROUND_NUMBER][OUTPUT_KEY_LENGTH / BITS_IN_BYTE];

        BitSet bitSet = BitSet.valueOf(key);

        BitSet leftHalfOfBitset = PC_1(bitSet, 0, PC_1_LENGTH / 2);

        BitSet rightHalfOfBitset = PC_1(bitSet, PC_1_LENGTH / 2, PC_1_LENGTH);

        for (int i = 0; i < ROUND_NUMBER; i++) {
            leftHalfOfBitset = leftShift(leftHalfOfBitset, bitsRotation[i]);
            rightHalfOfBitset = leftShift(rightHalfOfBitset, bitsRotation[i]);

            BitSet connectedBitSet = PC_2(leftHalfOfBitset, rightHalfOfBitset);

            roundKeys[i] = connectedBitSet.toByteArray();
        }
        return roundKeys;
    }

    private BitSet PC_1(BitSet bitSet, int start, int end) {
        BitSet permutedBitSet = new BitSet(PC_1_LENGTH / 2);
        for (int i = start; i < end; i++) {
            permutedBitSet.set(i % (PC_1_LENGTH / 2), bitSet.get(PC_1[i] - 1));
        }
        return permutedBitSet;
    }

    private BitSet leftShift(BitSet bitSet, int n) {
        long digit = bitSet.toLongArray()[0];

        final int sizeOfDigit = PC_1_LENGTH / 2;
        digit = (digit >> n) | (digit << (sizeOfDigit - n));

        return BitSet.valueOf(new long[]{digit});
    }

    private BitSet PC_2(BitSet leftHalfOfBitset, BitSet rightHalfOfBitset) {
        BitSet bitSet = new BitSet(OUTPUT_KEY_LENGTH);
        for (int i = 0; i < OUTPUT_KEY_LENGTH; i++) {
            int index = PC_2[i] - 1;

            if (index < PC_1_LENGTH / 2) {
                bitSet.set(i, leftHalfOfBitset.get(index));
            } else {
                bitSet.set(i, rightHalfOfBitset.get(index % (PC_1_LENGTH / 2)));
            }
        }
        return bitSet;
    }
}
