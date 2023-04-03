package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.cypher.impl.CFBCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;

import java.util.Objects;

public class CFBFabric implements SymmetricalBlockCypherFabric {

    @Override
    public SymmetricalBlockModeCypher create(SymmetricalBlockEncryptionAlgorithm algorithm, Object... args) {
        Objects.requireNonNull(args);

        int positionOfInitialVector = ArgPosition.IV.position;
        if (args.length <= positionOfInitialVector) {
            throw new IllegalArgumentException("Wrong args length. No init vector");
        }

        byte[] IV = (byte[])(args[positionOfInitialVector]);
        if (IV.length != algorithm.getOpenTextBlockSizeInBytes()) {
            throw new IllegalArgumentException("Wrong IV size");
        }
        return new CFBCypher(algorithm, IV);
    }
}
