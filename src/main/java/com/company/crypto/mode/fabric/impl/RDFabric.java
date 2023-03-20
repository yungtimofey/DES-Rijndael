package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.cypher.impl.OFBCypher;
import com.company.crypto.mode.cypher.impl.RDCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;

import java.util.Objects;

public class RDFabric implements SymmetricalBlockCypherFabric {
    @Override
    public SymmetricalBlockModeCypher create(SymmetricalBlockEncryptionAlgorithm algorithm, Object... args) {
        Objects.requireNonNull(args);

        int positionOfInitialVector = ArgPosition.IV.position;
        if (args.length <= positionOfInitialVector) {
            throw new IllegalArgumentException("Wrong args length. No init vector");
        }

        return new RDCypher(algorithm, (byte[])(args[positionOfInitialVector]));
    }
}
