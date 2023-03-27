package com.company.crypto.round.impl;

import com.company.crypto.round.RoundTransformer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public final class RoundTransformerDES extends RoundTransformer {
    private static final int HALF_SIZE = 32;
    private static final int[] E = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };
    private static final int[][] S = {
            {
                    14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
            },
            {
                    15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
            },
            {
                    10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
            },
            {
                    7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
            },
            {
                    2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
            },
            {
                    12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
            },
            {
                    4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
            },
            {
                    13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
            }
    };
    private static final int[] P = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    @Override
    public byte[] encode(byte[] inputBlock, byte[] roundKey, boolean isLastRound) {
        return doRound(inputBlock, roundKey, isLastRound);
    }

    @Override
    public byte[] decode(byte[] inputBlock, byte[] roundKey, boolean isLastRound) {
        return doRound(inputBlock, roundKey, isLastRound);
    }

    byte[] doRound(byte[] inputBlock, byte[] roundKey, boolean isLastRound) {
        BitSet inputBitSet = BitSet.valueOf(inputBlock);
        BitSet rightHalf = getRightHalf(inputBitSet);
        BitSet leftHalf = getLeftHalf(inputBitSet);

        BitSet expandedRightHalf = expandHalf(rightHalf);
        expandedRightHalf.xor(BitSet.valueOf(roundKey));

        final int currentGroupSize = 6;
        final int newGroupSize = 4;
        BitSet f = sPermutation(expandedRightHalf, currentGroupSize, newGroupSize);

        f = lastPermutation(f);

        f.xor(leftHalf);

        byte[] combinedArray = isLastRound
                ? combineTwoParts(f, rightHalf).toByteArray()
                : combineTwoParts(rightHalf, f).toByteArray();

        return combinedArray.length == inputBlock.length
                ? combinedArray
                : increaseArrayTo64Bit(combinedArray);
    }

    BitSet getLeftHalf(BitSet inputBitSet) {
        long[] inputArray = inputBitSet.toLongArray();
        long digit = (inputArray.length == 0) ? 0 : inputArray[0];

        digit &= (2L << HALF_SIZE - 1) - 1;
        return BitSet.valueOf(new long[]{digit});
    }

    BitSet getRightHalf(BitSet inputBitSet) {
        long[] inputArray = inputBitSet.toLongArray();
        long digit = (inputArray.length == 0) ? 0 : inputArray[0];

        digit = digit >> HALF_SIZE;
        digit &= (2L << HALF_SIZE) - 1;
        return BitSet.valueOf(new long[]{digit});
    }

    BitSet expandHalf(BitSet rightHalf) {
        BitSet expandedRightHalf = new BitSet(E.length);
        for (int i = 0; i < E.length; i++) {
            expandedRightHalf.set(i, rightHalf.get(E[i] - 1));
        }
        return expandedRightHalf;
    }

    @Override
    protected BitSet sPermutation(BitSet expandedHalf, int currentGroupSize, int newGroupSize) {
        final int columnBits = 2;
        final int rowSize = 1 << (currentGroupSize - columnBits);

        List<Integer> rows = getRows(expandedHalf, currentGroupSize);
        List<Integer> columns = getColumns(expandedHalf, currentGroupSize);

        BitSet reducedRightHalf = new BitSet();
        for (int i = 0, j = 0; i < rows.size(); i++) {
            int[] roundS = S[i];

            int row = rows.get(i);
            int column = columns.get(i);
            int positionInS = column * rowSize + row;

            int sDigit = roundS[positionInS];
            for (int k = 0; k < newGroupSize; k++) {
                boolean bit = ((sDigit >> (newGroupSize - 1 - k)) & 1) == 1;
                reducedRightHalf.set(j++, bit);
            }
        }
        return reducedRightHalf;
    }

    List<Integer> getRows(BitSet expandedRightHalf, int currentGroupSize) {
        List<Integer> rows = new ArrayList<>();

        final int columnBits = 2, rowsBits = currentGroupSize - columnBits;
        int i = 1;
        while (i < expandedRightHalf.length()) {
            int digit = 0;
            for (int j = 1; j <= rowsBits; j++) {
                int currentBit = expandedRightHalf.get(i + rowsBits - j) ? 1 : 0;
                digit += (1 << (j - 1)) * currentBit;
            }
            rows.add(digit);
            i += currentGroupSize;
        }

        return rows;
    }
    List<Integer> getColumns(BitSet expandedRightHalf, int currentGroupSize) {
        List<Integer> columns = new ArrayList<>();

        final int columnBits = 2;
        int i = 0;
        while (i < expandedRightHalf.length()) {
            int firstBitOfGroup = expandedRightHalf.get(i) ? 1 : 0;

            i += currentGroupSize - columnBits + 1;
            int secondBitOfGroup = expandedRightHalf.get(i) ? 1 : 0;

            int row = 2 * firstBitOfGroup + secondBitOfGroup;
            columns.add(row);

            i++;
        }
        return columns;
    }

    BitSet lastPermutation(BitSet rightHalf) {
        BitSet bitSet = new BitSet();
        for (int i = 0; i < P.length; i++) {
            bitSet.set(i, rightHalf.get(P[i] - 1));
        }
        return bitSet;
    }

    BitSet combineTwoParts(BitSet leftHalf, BitSet rightHalf) {
        BitSet combinedBitset = new BitSet();
        for (int i = 0; i < HALF_SIZE * 2; i++) {
            combinedBitset.set(i, (i < HALF_SIZE) ? leftHalf.get(i) : rightHalf.get(i % HALF_SIZE));
        }
        return combinedBitset;
    }

    byte[] increaseArrayTo64Bit(byte[] array) {
        byte[] increasedArray = {0, 0, 0, 0, 0, 0, 0, 0};
        System.arraycopy(array, 0, increasedArray, 0, array.length);
        return increasedArray;
    }
}
