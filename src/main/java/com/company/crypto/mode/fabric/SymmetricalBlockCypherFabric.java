package com.company.crypto.mode.fabric;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;


public interface SymmetricalBlockCypherFabric {
    SymmetricalBlockModeCypher create(
            SymmetricalBlockEncryptionAlgorithm algorithm,
            Object... args
    );
}
