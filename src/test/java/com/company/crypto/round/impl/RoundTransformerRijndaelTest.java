package com.company.crypto.round.impl;

import com.company.crypto.algorithm.impl.Rijndael;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculatorImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RoundTransformerRijndaelTest {
    static RoundTransformer roundTransformer;
    static RoundKeysGenerator roundKeysGenerator;
    static byte[][] roundKeys;


    @Test
    void checkEncodeAndDecode128BitsAll() {
        roundTransformer = new RoundTransformerRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_128,
                new GaloisFieldPolynomialsCalculatorImpl()
        );

        roundKeysGenerator = new RoundKeyGeneratorRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_128,
                Rijndael.RijndaelBlockSize.BIT_128
        );

        byte[] cipherKey = {1, 99, -1, 123, 67, 3, 76, 66, 1, 99, 41, 12, 67, 3, 76, 66};
        roundKeys = roundKeysGenerator.generate(cipherKey);

        byte[] inputBlock = {1, (byte) 255, 10, 123, 67, 3, 76, 66, 10, 99, 41, 12, 67, 3, 76, 66};
        byte[] inputBlockToCheck = Arrays.copyOf(inputBlock, inputBlock.length);

        byte[] encoded = roundTransformer.encode(inputBlock, roundKeys[0], false);
        byte[] decoded = roundTransformer.decode(encoded, roundKeys[0], false);

        assertArrayEquals(inputBlockToCheck, decoded);
    }

    @Test
    void checkAndDecode192BitsAnd128Bits() {
        roundTransformer = new RoundTransformerRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_192,
                new GaloisFieldPolynomialsCalculatorImpl()
        );

        roundKeysGenerator = new RoundKeyGeneratorRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_192,
                Rijndael.RijndaelBlockSize.BIT_128
        );

        byte[] cipherKey = {1, 99, (byte) 129, 123, 67, 3, 76, 66, 1, (byte) 255, 41, 12, 67, 3, 76, 66};
        roundKeys = roundKeysGenerator.generate(cipherKey);

        byte[] inputBlock = {
                1, 99, 10, 123, 67, 3, 76, 66, 10, 99, 41, 12,
                (byte) 255, 99, 10, 123, 67, -10, 76, 66, 10, 99, 41, 12
        };
        byte[] inputBlockToCheck = Arrays.copyOf(inputBlock, inputBlock.length);

        byte[] encoded = roundTransformer.encode(inputBlock, roundKeys[0], true);
        byte[] decoded = roundTransformer.decode(encoded, roundKeys[0], true);

        assertArrayEquals(inputBlockToCheck, decoded);
    }

    @Test
    void checkEncodeAndDecode256BitsAnd256Bits() {
        roundTransformer = new RoundTransformerRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_256,
                new GaloisFieldPolynomialsCalculatorImpl()
        );

        roundKeysGenerator = new RoundKeyGeneratorRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_256,
                Rijndael.RijndaelBlockSize.BIT_256
        );

        byte[] cipherKey = {1, 99, (byte) 129, 123, 67, 3, 76, 66, 1, (byte) 255, 41, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 67, 3, 7, 6, 1, (byte) 255, 41, 12, -72, 3, 76, 66
        };
        roundKeys = roundKeysGenerator.generate(cipherKey);

        byte[] inputBlock = {
                9, 69, (byte) 129, (byte) 129, 67, 38, 76, -66, 1, (byte) 255, 1, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 17, 0, 7, 6, 1, (byte) 255, 41, 12, -72, 3, 76, 6
        };

        byte[] inputBlockToCheck = Arrays.copyOf(inputBlock, inputBlock.length);


        byte[] encoded = roundTransformer.encode(inputBlock, roundKeys[0], true);
        byte[] decoded = roundTransformer.decode(encoded, roundKeys[0], true);

        assertArrayEquals(inputBlockToCheck, decoded);
    }

    @Test
    void checkEncodeAndDecode256BitsAnd192Bits() {
        roundTransformer = new RoundTransformerRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_256,
                new GaloisFieldPolynomialsCalculatorImpl()
        );

        roundKeysGenerator = new RoundKeyGeneratorRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_256,
                Rijndael.RijndaelBlockSize.BIT_192
        );

        byte[] cipherKey = {1, 99, (byte) 129, -123, 67, 3, 0, 66, 1, (byte) 255, 41, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 67, -93, -7, -6
        };
        roundKeys = roundKeysGenerator.generate(cipherKey);

        byte[] inputBlock = {
                9, 69, (byte) 129, (byte) 129, 67, 38, 76, -66, 1, (byte) 255, 1, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 17, 0, 7, 6, 1, (byte) 255, 41, 12, -72, 3, 76, 6
        };

        byte[] inputBlockToCheck = Arrays.copyOf(inputBlock, inputBlock.length);

        byte[] encoded = roundTransformer.encode(inputBlock, roundKeys[0], false);
        byte[] decoded = roundTransformer.decode(encoded, roundKeys[0], false);

        assertArrayEquals(inputBlockToCheck, decoded);
    }

    @Test
    void checkEncodeAndDecode128And256Bits() {
        roundTransformer = new RoundTransformerRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_128,
                new GaloisFieldPolynomialsCalculatorImpl()
        );

        roundKeysGenerator = new RoundKeyGeneratorRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_128,
                Rijndael.RijndaelBlockSize.BIT_256
        );

        byte[] cipherKey = {8, -99, (byte) -129, -123, -67, -3, -76, 66, 1, (byte) 255, 41, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 67, 3, 7, 6, 1, (byte) 255, 41, 12, -72, 3, 76, -66
        };
        roundKeys = roundKeysGenerator.generate(cipherKey);

        byte[] inputBlock = {1, (byte) -255, 10, -123, -67, -3, -76, 66, 0, -99, -41, 12, 67, 3, 76, 66};
        byte[] inputBlockToCheck = Arrays.copyOf(inputBlock, inputBlock.length);


        byte[] encoded = roundTransformer.encode(inputBlock, roundKeys[13], true);
        byte[] decoded = roundTransformer.decode(encoded, roundKeys[13], true);

        assertArrayEquals(inputBlockToCheck, decoded);
    }

    @Test
    void checkEncodeAndDecode192And256Bits() {
        roundTransformer = new RoundTransformerRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_192,
                new GaloisFieldPolynomialsCalculatorImpl()
        );

        roundKeysGenerator = new RoundKeyGeneratorRijndael(
                283,
                Rijndael.RijndaelBlockSize.BIT_192,
                Rijndael.RijndaelBlockSize.BIT_256
        );

        byte[] cipherKey = {8, -99, (byte) -129, -123, -67, -3, -76, 66, 1, (byte) 255, 41, 12, 67, 3, 76, 66,
                -1, 99, (byte) 129, -123, 67, 3, 7, 6, 1, (byte) 255, 41, 12, -72, 3, 76, -66
        };
        roundKeys = roundKeysGenerator.generate(cipherKey);

        byte[] inputBlock = {
                1, 99, 10, -123, 67, 3, 0, -1, 10, 99, 41, -12,
                (byte) 255, -99, 10, -123, 67, -10, 76, 66, -10, -99, 41, 12
        };

        byte[] inputBlockToCheck = Arrays.copyOf(inputBlock, inputBlock.length);

        byte[] encoded = roundTransformer.encode(inputBlock, roundKeys[2], true);
        byte[] decoded = roundTransformer.decode(encoded, roundKeys[2], true);

        assertArrayEquals(inputBlockToCheck, decoded);
    }

}