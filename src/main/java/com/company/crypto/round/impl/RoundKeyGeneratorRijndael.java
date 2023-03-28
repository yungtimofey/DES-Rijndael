package com.company.crypto.round.impl;

import com.company.crypto.algorithm.impl.Rijndael;
import com.company.crypto.round.RoundKeysGenerator;

public final class RoundKeyGeneratorRijndael implements RoundKeysGenerator {
    private final int openTextSize;
    private final int cipherKeySize;

    public RoundKeyGeneratorRijndael(
            Rijndael.RijndaelBlockSize openTextSize,
            Rijndael.RijndaelBlockSize cipherKeySize) {
        this.openTextSize = openTextSize.bitsNumber;
        this.cipherKeySize = cipherKeySize.bitsNumber;
    }


    @Override
    public byte[][] generate(byte[] cipherKey) {
        return new byte[0][];
    }
}
