package com.company.crypto.mode.cypher;

import java.io.File;

/**
 * Its class is used by Cypher. Makes encode and decode
 */
public interface SymmetricalBlockCypher {
    void encode(File inputFile, File outputFile);
    void decode(File inputFile, File outputFile);
}
