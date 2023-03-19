package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;
import com.company.crypto.mode.cypher.impl.ECBCypher;

public class ECBFabric implements SymmetricalBlockCypherFabric {
    @Override
    public SymmetricalBlockModeCypher create(
            SymmetricalBlockEncryptionAlgorithm algorithm,
            Object... args) {

        return new ECBCypher(algorithm);
    }
}
