package com.company.crypto.algorithm.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;

import java.util.Objects;

class FeistelNetwork implements SymmetricalBlockEncryptionAlgorithm {
    private static final int ROUND_NUMBER = 16;

    private final RoundKeysGenerator roundKeysGenerator;
    private final RoundTransformer roundTransformer;
    private byte[] cipherKey64Bit;

    public FeistelNetwork(RoundKeysGenerator roundKeysGenerator, RoundTransformer roundTransformer) {
        this.roundKeysGenerator = roundKeysGenerator;
        this.roundTransformer = roundTransformer;
    }

    @Override
    public byte[] encode(byte[] inputBlock) {
        Objects.requireNonNull(cipherKey64Bit);
        Objects.requireNonNull(inputBlock);

        byte[][] roundKeys = roundKeysGenerator.generate(cipherKey64Bit);
        for (int i = 0; i < ROUND_NUMBER; i++) {
            byte[] roundKey = roundKeys[i];
            inputBlock = roundTransformer.encode(inputBlock, roundKey, i == ROUND_NUMBER-1);
        }
        return inputBlock;
    }

    @Override
    public byte[] decode(byte[] inputBlock) {
        Objects.requireNonNull(cipherKey64Bit);
        Objects.requireNonNull(inputBlock);

        byte[][] roundKeys = roundKeysGenerator.generate(cipherKey64Bit);
        for (int i = ROUND_NUMBER-1; i >= 0; i--) {
            byte[] roundKey = roundKeys[i];
            inputBlock = roundTransformer.decode(inputBlock, roundKey, i == 0);
        }
        return inputBlock;
    }

    @Override
    public void setKey(byte[] cipherKey) {
        this.cipherKey64Bit = cipherKey;
    }

    @Override
    public int getOpenTextBlockSizeInBytes() {
        return 8;
    }
}
