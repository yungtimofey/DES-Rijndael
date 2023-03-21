package com.company.crypto.mode.cypher.impl;

import com.company.crypto.Cypher;
import com.company.crypto.algorithm.impl.DES;
import com.company.crypto.mode.SymmetricalBlockMode;
import com.company.crypto.round.impl.RoundKeysGeneratorImpl;
import com.company.crypto.round.impl.RoundTransformerImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;

class CFBCypherTest {
    static Cypher cypher;
    static byte[] key;
    static byte[] IV;

    @BeforeAll
    static void init() {
        BitSet bitSet = init(64, 1, 2, 3, 4, 32, 34, 37, 41, 42, 54, 56, 57, 58);
        key = bitSet.toByteArray();

        IV = init(64, 1, 2, 3, 4, 32, 34, 37, 41, 42, 54, 56, 57, 58, 62).toByteArray();

        cypher = Cypher.build(
                key,
                SymmetricalBlockMode.CFB,
                new DES(new RoundKeysGeneratorImpl(), new RoundTransformerImpl()),
                IV
        );
    }

    @Test
    void encodeAndDecodeTextFile() throws IOException {
        String input = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\1.txt";
        String encoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\2.txt";
        String decoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\3.txt";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    @Test
    void encodeAndDecodeImage() throws IOException {
        String input = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\1.jpg";
        String encoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\2.jpg";
        String decoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\3.jpg";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    @Test
    void encodeAndDecodeVideo() throws IOException {
        String input = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\Patrick.mp4";
        String encoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\2.mp4";
        String decoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\3.mp4";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    @Test
    void encodeAndDecodeLongVideo() throws IOException {
        String input = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\song.mp4";
        String encoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\2.mp4";
        String decoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\3.mp4";

        File inputFile = new File(input);
        File encodedFile = new File(encoded);
        File decodedFile = new File(decoded);

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

    @Test
    void encodeAndDecodeSong() throws IOException {
        String input = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\kenny.mp3";
        String encoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\2.mp3";
        String decoded = "C:\\Users\\Timofey.LAPTOP-KQGJSA46\\Desktop\\des\\3.mp3";

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