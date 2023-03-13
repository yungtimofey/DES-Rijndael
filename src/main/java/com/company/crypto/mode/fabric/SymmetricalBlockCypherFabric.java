package com.company.crypto.mode.fabric;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockCypher;


public interface SymmetricalBlockCypherFabric {
    SymmetricalBlockCypher create(
            SymmetricalBlockEncryptionAlgorithm algorithm,
            byte[] key,
            Object... args
    );
}
