package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.cypher.impl.CBCCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;

public class CBCFabric implements SymmetricalBlockCypherFabric {
    @Override
    public SymmetricalBlockModeCypher create(SymmetricalBlockEncryptionAlgorithm algorithm, Object... args) {
        int positionOfInitialVector = ArgPosition.IV.position;
        return new CBCCypher(algorithm, (byte[])(args[positionOfInitialVector]));
    }
}
