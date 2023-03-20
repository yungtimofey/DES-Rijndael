package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.cypher.impl.RDPlusHCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;

import java.util.Objects;

public class RDPlusHFabric implements SymmetricalBlockCypherFabric {
    @Override
    public SymmetricalBlockModeCypher create(SymmetricalBlockEncryptionAlgorithm algorithm, Object... args) {
        Objects.requireNonNull(args);

        int positionOfInitialVector = ArgPosition.IV.position;
        int positionOfHash = ArgPosition.HASH.position;
        if (args.length <= positionOfInitialVector || args.length <= positionOfHash) {
            throw new IllegalArgumentException("Wrong args length. No init vector");
        }

        return new RDPlusHCypher(algorithm,  (byte[]) args[positionOfInitialVector], (byte[])args[positionOfHash]);
    }
}