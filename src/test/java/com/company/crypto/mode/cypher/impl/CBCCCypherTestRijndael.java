package com.company.crypto.mode.cypher.impl;

import com.company.crypto.Cypher;
import com.company.crypto.algorithm.impl.Rijndael;
import com.company.crypto.mode.SymmetricalBlockMode;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;
import com.company.crypto.round.impl.RoundKeyGeneratorRijndael;
import com.company.crypto.round.impl.RoundTransformerRijndael;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculatorImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;

public class CBCCCypherTestRijndael {

    static Cypher cypher;
    static byte[] key;
    static byte[] IV;

    @BeforeAll
    static void init() {
        byte[] cipherKey = {1, 99, (byte) 129, -123, 67, 3, 0, 66, 1, (byte) 255, 41, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 67, -93, -7, -6
        };
        key = cipherKey;

        GaloisFieldPolynomialsCalculator galoisFieldPolynomialsCalculator = new GaloisFieldPolynomialsCalculatorImpl();
        List<Integer> list = galoisFieldPolynomialsCalculator.getAllIrreduciblePolynomials();

        RoundTransformer roundTransformer = new RoundTransformerRijndael(
                list.get(0),
                Rijndael.RijndaelBlockSize.BIT_256,
                new GaloisFieldPolynomialsCalculatorImpl()
        );

        RoundKeysGenerator roundKeysGenerator = new RoundKeyGeneratorRijndael(
                list.get(0),
                Rijndael.RijndaelBlockSize.BIT_256,
                Rijndael.RijndaelBlockSize.BIT_192
        );

        IV = new byte[]{9, 69, (byte) 129, (byte) 129, 67, 38, 76, -66, 1, (byte) 255, 1, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 17, 0, 7, 6, 1, (byte) 255, 41, 12, -72, 3, 76, 6};

        cypher = Cypher.build(
                key,
                SymmetricalBlockMode.CBC,
                Rijndael.getInstance(
                    roundKeysGenerator,
                    roundTransformer,
                    Rijndael.RijndaelBlockSize.BIT_256,
                    Rijndael.RijndaelBlockSize.BIT_192
                ),
                IV
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

        encodedFile.delete();
        decodedFile.delete();

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

        encodedFile.delete();
        decodedFile.delete();

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

        encodedFile.delete();
        decodedFile.delete();

        cypher.encode(inputFile, encodedFile);
        cypher.decode(encodedFile, decodedFile);

        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
    }

//    @Test
//    void encodeAndDecodeLongVideo() throws IOException {
//        String input = "song.mp4";
//        String encoded = "2.mp4";
//        String decoded = "3.mp4";
//
//        File inputFile = new File(input);
//        File encodedFile = new File(encoded);
//        File decodedFile = new File(decoded);
//
//        cypher.encode(inputFile, encodedFile);
//        cypher.decode(encodedFile, decodedFile);
//
//        assert(Files.mismatch(Path.of(input), Path.of(decoded)) == -1);
//    }

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
