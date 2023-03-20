package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.cypher.impl.CTRCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;

public class CTRFabric implements SymmetricalBlockCypherFabric {
    @Override
    public SymmetricalBlockModeCypher create(SymmetricalBlockEncryptionAlgorithm algorithm, Object... args) {
        int positionOfStartIndex = ArgPosition.INDEX_FOR_CTR.position;
        return new CTRCypher(algorithm, (int) args[positionOfStartIndex]);
    }
}
