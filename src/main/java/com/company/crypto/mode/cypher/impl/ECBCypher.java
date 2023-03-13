package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockCypher;

import java.io.File;

public final class ECBCypher implements SymmetricalBlockCypher {
    private final SymmetricalBlockEncryptionAlgorithm algorithm;
    private final byte[] key;

    public ECBCypher(SymmetricalBlockEncryptionAlgorithm algorithm, byte[] key) {
        this.algorithm = algorithm;
        this.key = key;
    }

    @Override
    public void encode(File inputFile, File outputFile) {
        System.out.println("Encode");
    }

    @Override
    public void decode(File inputFile, File outputFile) {
        System.out.println("Deocode");
    }

    @Override
    public void close()  {
        // Executor
    }
}
