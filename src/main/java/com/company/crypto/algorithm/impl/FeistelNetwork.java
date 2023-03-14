package com.company.crypto.algorithm.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;

public class FeistelNetwork implements SymmetricalBlockEncryptionAlgorithm {
    private static final int ROUND_NUMBER = 16;

    private final RoundKeysGenerator roundKeysGenerator;
    private final RoundTransformer roundTransformer;

    public FeistelNetwork(RoundKeysGenerator roundKeysGenerator, RoundTransformer roundTransformer) {
        this.roundKeysGenerator = roundKeysGenerator;
        this.roundTransformer = roundTransformer;
    }

    @Override
    public byte[] encode(byte[] array, byte[] key) {
        byte[][] roundKeys = roundKeysGenerator.generate(key);

        for (int i = 0; i < ROUND_NUMBER; i++) {
            byte[] roundKey = roundKeys[i];

        }

        return null;
    }

    @Override
    public byte[] decode(byte[] array, byte[] key) {
        byte[][] keys = roundKeysGenerator.generate(key);

        for (int i = 0; i < ROUND_NUMBER; i++) {

        }

        return null;
    }


}
