package com.company.crypto.algorithm.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;

public class FeistelNetwork implements SymmetricalBlockEncryptionAlgorithm {
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
        return null;
    }

    @Override
    public byte[] decode(byte[] outputBlock64Bit) {
        return null;
    }


}
