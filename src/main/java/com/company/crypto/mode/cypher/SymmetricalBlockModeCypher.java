package com.company.crypto.mode.cypher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Its class is used by Cypher. Makes encode and decode
 */
public interface SymmetricalBlockModeCypher extends Closeable {
    void encode(File inputFile, File outputFile) throws IOException;
    void decode(File inputFile, File outputFile) throws IOException;
}
