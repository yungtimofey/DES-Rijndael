package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.cypher.impl.OFBCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;

public class CFBFabric implements SymmetricalBlockCypherFabric {

    @Override
    public SymmetricalBlockModeCypher create(SymmetricalBlockEncryptionAlgorithm algorithm, Object... args) {
        int positionOfInitialVector = ArgPosition.IV.position;
        return new OFBCypher(algorithm, (byte[])(args[positionOfInitialVector]));
    }
}
