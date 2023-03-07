package com.company.crypto;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockCypher;
import com.company.crypto.mode.SymmetricalBlockMode;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;
import com.company.crypto.mode.fabric.impl.ECBFabric;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Main encoder/decoder. Get symmetric algorithm (64 bit encoder/decoder), mode, and other param.
 **/
public final class Cypher implements Closeable {
    private final static Map<SymmetricalBlockMode, SymmetricalBlockCypherFabric> modeAndItsFabric = new HashMap<>();
    {
        modeAndItsFabric.put(SymmetricalBlockMode.ECB, new ECBFabric());
    }

    public static Cypher build(byte[] key,
                               SymmetricalBlockMode symmetricalBlockMode,
                               Class<? extends SymmetricalBlockEncryptionAlgorithm> algorithmClass,
                               Object ... args) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(symmetricalBlockMode);
        Objects.requireNonNull(algorithmClass);
        return new Cypher(key, symmetricalBlockMode, algorithmClass, args);
    }

    private final SymmetricalBlockCypher symmetricalBlockCypher;
    private Cypher(byte[] key,
                   SymmetricalBlockMode mode,
                   Class<? extends SymmetricalBlockEncryptionAlgorithm> algorithmClass,
                   Object ... args) {

        SymmetricalBlockCypherFabric cypherFabric = modeAndItsFabric.get(mode);
        this.symmetricalBlockCypher = cypherFabric.create(algorithmClass, key, args);
    }


    public void encode(File inputFile, File outputFile) {
        symmetricalBlockCypher.encode(inputFile, outputFile);
    }

    public void decode(File inputFile, File outputFile) {
        symmetricalBlockCypher.decode(inputFile, outputFile);
    }

    @Override
    public void close() {
        // TODO для executor
        try {
            symmetricalBlockCypher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
