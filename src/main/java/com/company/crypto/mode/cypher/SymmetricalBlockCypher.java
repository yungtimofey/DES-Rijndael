package com.company.crypto.mode.cypher;

import java.io.Closeable;
import java.io.File;

/**
 * Its class is used by Cypher. Makes encode and decode
 */
public interface SymmetricalBlockCypher extends Closeable {
    void encode(File inputFile, File outputFile);
    void decode(File inputFile, File outputFile);
}
