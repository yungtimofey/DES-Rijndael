package com.company.crypto.mode.cypher.impl;

import com.company.crypto.Cypher;
import com.company.crypto.algorithm.impl.DES;
import com.company.crypto.mode.SymmetricalBlockMode;
import com.company.crypto.round.impl.RoundKeysGeneratorDES;
import com.company.crypto.round.impl.RoundTransformerDES;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;

class CTRCypherTestDES {
    static Cypher cypher;
    static byte[] key;

    @BeforeAll
    static void init() {
        BitSet bitSet = init(64, 1, 2, 3, 4, 32, 34, 37, 41, 42, 54, 56, 57, 58);
        key = bitSet.toByteArray();

        cypher = Cypher.build(
                key,
                SymmetricalBlockMode.CTR,
                new DES(new RoundKeysGeneratorDES(), new RoundTransformerDES()),
                null, 2
        );
    }

    @Test
    void encodeAndDecodeTextFile() throws IOException {
        String input = "1.txt";
        String encoded = "2.txt";
        String decoded = "3.txt";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    @Test
    void encodeAndDecodeImage() throws IOException {
        String input = "1.jpg";
        String encoded = "2.jpg";
        String decoded = "3.jpg";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    @Test
    void encodeAndDecodeVideo() throws IOException {
        String input = "Patrick.mp4";
        String encoded = "2.mp4";
        String decoded = "3.mp4";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    @Test
    void encodeAndDecodeLongVideo() throws IOException {
        String input = "song.mp4";
        String encoded = "2.mp4";
        String decoded = "3.mp4";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    private static BitSet init(int size, int ... indexes) {
        BitSet bitSet = new BitSet(size);
        for (int i = 0; i < size; i++) {
            bitSet.set(i, false);
        }
        for (int index : indexes) {
            bitSet.set(index-1, true);
        }
        return bitSet;
    }


    @AfterAll
    static void finish() {
        if (cypher != null) {
            cypher.close();
        }
    }
}