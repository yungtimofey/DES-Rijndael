package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;
import com.company.crypto.mode.cypher.impl.ECBCypher;

public class ECBFabric implements SymmetricalBlockCypherFabric {
    @Override
    public SymmetricalBlockCypher create(
            SymmetricalBlockEncryptionAlgorithm algorithm,
            byte[] key,
            Object... args) {

        return new ECBCypher(algorithm, key);
    }
}
