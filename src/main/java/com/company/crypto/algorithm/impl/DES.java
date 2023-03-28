package com.company.crypto.algorithm.impl;

import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;

import java.util.BitSet;

public final class DES extends FeistelNetwork {
    private static final int OPEN_TEXT_SIZE = 64;
    private static final int SIZE_OF_OPEN_TEXT_BYTE_ARRAY = OPEN_TEXT_SIZE / Byte.SIZE;
    private static final int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
    };
    private static final int[] reverseIP = {
            40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25
    };

    public DES(RoundKeysGenerator roundKeysGenerator, RoundTransformer roundTransformer) {
        super(roundKeysGenerator, roundTransformer);
    }

    /**
     * @return 64 encoded text
     */
    @Override
    public byte[] encode(byte[] inputBlock) {
        inputBlock = IP(BitSet.valueOf(inputBlock));

        inputBlock = super.encode(inputBlock);

        inputBlock = reverseIP(BitSet.valueOf(inputBlock));

        if (inputBlock.length < SIZE_OF_OPEN_TEXT_BYTE_ARRAY) {
            inputBlock = increaseArrayTo64Bit(inputBlock);
        }
        return inputBlock;
    }

    /**
     * @return 64 bits
     */
    @Override
    public byte[] decode(byte[] inputBlock) {
        inputBlock = IP(BitSet.valueOf(inputBlock));

        inputBlock = super.decode(inputBlock);

        inputBlock = reverseIP(BitSet.valueOf(inputBlock));

        return inputBlock;
    }

    /**
     * @return  64 bits permuted array
     **/
    byte[] IP(BitSet inputBitset) {
        BitSet permutedBitset = new BitSet();
        for (int i = 0; i < IP.length; i++) {
            permutedBitset.set(i, inputBitset.get(IP[i] - 1));
        }

        byte[] permutedBitSetArray = permutedBitset.toByteArray();
        return permutedBitSetArray.length == SIZE_OF_OPEN_TEXT_BYTE_ARRAY
                ? permutedBitSetArray
                : increaseArrayTo64Bit(permutedBitSetArray);
    }

    /**
     * @return  64 bits permuted array
     **/
    byte[] reverseIP(BitSet inputBitset) {
        BitSet permutedBitset = new BitSet(reverseIP.length);
        for (int i = 0; i < reverseIP.length; i++) {
            permutedBitset.set(i, inputBitset.get(reverseIP[i] - 1));
        }

        byte[] permutedBitSetArray = permutedBitset.toByteArray();
        return permutedBitSetArray.length == SIZE_OF_OPEN_TEXT_BYTE_ARRAY
                ? permutedBitSetArray
                : increaseArrayTo64Bit(permutedBitSetArray);
    }

    byte[] increaseArrayTo64Bit(byte[] array) {
        byte[] increasedArray = {0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(array, 0, increasedArray, 0, array.length);
        return increasedArray;
    }
}
