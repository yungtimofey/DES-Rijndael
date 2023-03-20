package com.company.crypto;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.SymmetricalBlockMode;
import com.company.crypto.mode.fabric.SymmetricalBlockCypherFabric;
import com.company.crypto.mode.fabric.impl.CBCFabric;
import com.company.crypto.mode.fabric.impl.ECBFabric;
import com.company.crypto.mode.fabric.impl.OFBFabric;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Main encoder/decoder. Get symmetric algorithm (64 bit encoder/decoder), mode, and other param.
 **/
public final class Cypher implements Closeable {
    private static final Map<SymmetricalBlockMode, SymmetricalBlockCypherFabric> modeAndItsFabric = new EnumMap<>(SymmetricalBlockMode.class);
    static {
        modeAndItsFabric.put(SymmetricalBlockMode.ECB, new ECBFabric());
        modeAndItsFabric.put(SymmetricalBlockMode.CBC, new CBCFabric());
        modeAndItsFabric.put(SymmetricalBlockMode.OFB, new OFBFabric());
    }

    public static Cypher build(byte[] key,
                               SymmetricalBlockMode symmetricalBlockMode,
                               SymmetricalBlockEncryptionAlgorithm algorithm,
                               Object ... args) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(symmetricalBlockMode);
        Objects.requireNonNull(algorithm);

        return new Cypher(key, symmetricalBlockMode, algorithm, args);
    }

    private final SymmetricalBlockModeCypher symmetricalBlockCypher;
    private final SymmetricalBlockEncryptionAlgorithm algorithm;
    private Cypher(byte[] key,
                   SymmetricalBlockMode mode,
                   SymmetricalBlockEncryptionAlgorithm algorithm,
                   Object ... args) {
        this.algorithm = algorithm;
        algorithm.setKey(key);

        SymmetricalBlockCypherFabric cypherFabric = modeAndItsFabric.get(mode);
        this.symmetricalBlockCypher = cypherFabric.create(algorithm, args);
    }

    public void encode(File inputFile, File outputFile) throws IOException {
        Objects.requireNonNull(inputFile);
        Objects.requireNonNull(outputFile);

        outputFile.delete();
        symmetricalBlockCypher.encode(inputFile, outputFile);
    }

    public void decode(File inputFile, File outputFile) throws IOException {
        Objects.requireNonNull(inputFile);
        Objects.requireNonNull(outputFile);

        outputFile.delete();
        symmetricalBlockCypher.decode(inputFile, outputFile);
    }

    public void setKey(byte[] key) {
        this.algorithm.setKey(key);
    }

    @Override
    public void close() {
        symmetricalBlockCypher.close();
    }
}
