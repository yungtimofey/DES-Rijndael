package com.company.crypto.round.impl;

import com.company.crypto.round.RoundTransformer;

import java.util.BitSet;

public class RoundTransformerRijndael implements RoundTransformer {
    public RoundTransformerRijndael() {

    }

    @Override
    public byte[] encode(byte[] inputBlock, byte[] roundKey, boolean isLastRound) {
        return new byte[0];
    }

    @Override
    public byte[] decode(byte[] inputBlock, byte[] roundKey, boolean isLastRound) {
        return new byte[0];
    }


    protected BitSet sPermutation(BitSet expandedHalf, int currentGroupSize, int newGroupSize) {
        return null;
    }


    public static byte[] generateSBlock(int irreduciblePolynomial) {
        // TODO: save
        return new byte[0];
    }

    public static byte[] generateRCON(int cipherKeySize) {
        return new byte[0];
    }
}
