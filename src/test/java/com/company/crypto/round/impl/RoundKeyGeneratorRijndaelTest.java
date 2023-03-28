package com.company.crypto.round.impl;

import com.company.crypto.algorithm.impl.Rijndael;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.exception.WrongIrreduciblePolynomialException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoundKeyGeneratorRijndaelTest {
    static RoundKeyGeneratorRijndael roundKeyGeneratorRijndael;

    @BeforeAll
    static void init() {
        roundKeyGeneratorRijndael = new RoundKeyGeneratorRijndael(
                Rijndael.RijndaelBlockSize.BIT_128,
                Rijndael.RijndaelBlockSize.BIT_128,
                283
        );
    }

    @Test
    void checkSubByte() throws WrongIrreduciblePolynomialException {
        int[] toSubInt = {207, 79, 60, 9};
        byte[] toSubByte = new byte[toSubInt.length];

        for (int i = 0; i < toSubByte.length; i++) {
            toSubByte[i] = GaloisFieldPolynomialsCalculator.convertIntToByte(toSubInt[i]);
        }

        Rijndael.subByte(toSubByte, 283);

        int[] toCheck = {138, 132, 235, 1};
        for (int i = 0; i < toCheck.length; i++) {
            int translated = GaloisFieldPolynomialsCalculator.convertByteToInt(toSubByte[i]);
            if (translated != toCheck[i]) {
                assert (false);
            }
        }
        assert (true);
    }

    @Test
    void checkKey() {
        int[] array = {43, 126, 21, 22, 40, 174, 210, 166, 171, 247, 21, 136, 9, 207, 79, 60};

        byte[] keyC = new byte[16];
        for (int i = 0; i < 16; i++) {
            keyC[i] = GaloisFieldPolynomialsCalculator.convertIntToByte(array[i]);
        }

        roundKeyGeneratorRijndael.generate(keyC);

        int[] W4check = {160, 250, 254, 23};
        int[] W5check = {136, 84, 44, 177};

        byte[] W4 = roundKeyGeneratorRijndael.W[4];
        byte[] W5 = roundKeyGeneratorRijndael.W[5];

        for (int i = 0; i < W4check.length; i++) {
            int translated = GaloisFieldPolynomialsCalculator.convertByteToInt(W4[i]);
            if (W4check[i] != translated) {
                assert (false);
            }
        }

        for (int i = 0; i < W5check.length; i++) {
            int translated = GaloisFieldPolynomialsCalculator.convertByteToInt(W5[i]);
            if (W5check[i] != translated) {
                assert (false);
            }
        }

        assert (true);

    }
}