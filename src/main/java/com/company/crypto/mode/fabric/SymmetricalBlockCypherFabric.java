package com.company.crypto.mode.fabric;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;


public interface SymmetricalBlockCypherFabric {
    enum ArgPosition {
        IV(0), INDEX_FOR_CTR(1), HASH(2);

        public final int position;
        ArgPosition(int position) {
            this.position = position;
        }
    }


    SymmetricalBlockModeCypher create(
            SymmetricalBlockEncryptionAlgorithm algorithm,
            Object... args
    );
}
