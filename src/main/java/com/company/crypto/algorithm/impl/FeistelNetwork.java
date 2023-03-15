package com.company.crypto.algorithm.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;

import java.util.BitSet;

class FeistelNetwork implements SymmetricalBlockEncryptionAlgorithm {
    private static final int ROUND_NUMBER = 16;

    private final RoundKeysGenerator roundKeysGenerator;
    private final RoundTransformer roundTransformer;
    private byte[] key64Bit;

    public FeistelNetwork(RoundKeysGenerator roundKeysGenerator, RoundTransformer roundTransformer) {
        this.roundKeysGenerator = roundKeysGenerator;
        this.roundTransformer = roundTransformer;
    }

    public void setKey64Bit(byte[] key64Bit) {
        this.key64Bit = key64Bit;
    }

    @Override
    public byte[] encode(byte[] inputBlock64Bit) {
        byte[][] roundKeys = roundKeysGenerator.generate(key64Bit);
        for (int i = 0; i < ROUND_NUMBER; i++) {
            byte[] roundKey = roundKeys[i];
            inputBlock64Bit = roundTransformer.doRound(inputBlock64Bit, roundKey, i == ROUND_NUMBER-1);
        }
        return inputBlock64Bit;
    }

    @Override
    public byte[] decode(byte[] inputBlock64Bit) {
        byte[][] roundKeys = roundKeysGenerator.generate(key64Bit);
        for (int i = ROUND_NUMBER-1; i >= 0; i--) {
            byte[] roundKey = roundKeys[i];
            inputBlock64Bit = roundTransformer.doRound(inputBlock64Bit, roundKey, i == 0);
        }
        return inputBlock64Bit;
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
