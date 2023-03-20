package com.company.crypto.mode.fabric.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.cypher.impl.CTRCypher;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;

import java.util.Objects;

public class CTRFabric implements SymmetricalBlockCypherFabric {
    @Override
    public SymmetricalBlockModeCypher create(SymmetricalBlockEncryptionAlgorithm algorithm, Object... args) {
        Objects.requireNonNull(args);

        int positionOfStartIndex = ArgPosition.INDEX_FOR_CTR.position;
        if (args.length <= positionOfStartIndex) {
            throw new IllegalArgumentException("Wrong args length. No start index");
        }

        return new CTRCypher(algorithm, (int) args[positionOfStartIndex]);
    }
}
