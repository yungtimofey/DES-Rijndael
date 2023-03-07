package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockCypher;

import java.io.File;
import java.io.IOException;

public final class ECBCypher implements SymmetricalBlockCypher {
    private final Class<? extends SymmetricalBlockEncryptionAlgorithm> algorithmClass;
    private final byte[] key;

    public ECBCypher(Class<? extends SymmetricalBlockEncryptionAlgorithm> algorithmClass, byte[] key) {
        this.algorithmClass = algorithmClass;
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
