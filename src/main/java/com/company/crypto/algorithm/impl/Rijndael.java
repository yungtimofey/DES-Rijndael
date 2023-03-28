package com.company.crypto.algorithm.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculatorImpl;
import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

import java.util.HashMap;

public final class Rijndael implements SymmetricalBlockEncryptionAlgorithm {
    private static final HashMap<Integer, byte[]> sBoxAndItsIrreduciblePolynomial = new HashMap<>();
    private static final GaloisFieldPolynomialsCalculator galoisFieldPolynomialsCalculator = new GaloisFieldPolynomialsCalculatorImpl();

    public static byte[] getSBox(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (!sBoxAndItsIrreduciblePolynomial.containsKey(irreduciblePolynomial)) {
            sBoxAndItsIrreduciblePolynomial.put(irreduciblePolynomial, generateSBox(irreduciblePolynomial));
        }
        return sBoxAndItsIrreduciblePolynomial.get(irreduciblePolynomial);
    }
    private static byte[] generateSBox(int irreduciblePolynomial) {
        final int sBoxSize = 16;

        byte[] sBox = new byte[sBoxSize * sBoxSize];
        for (int i = 0; i < sBox.length; i++) {
            byte toReverse = GaloisFieldPolynomialsCalculator.convertIntToByte(i);
            byte reversed = galoisFieldPolynomialsCalculator.getReverse(toReverse);

            int toMakeAffineTransform = GaloisFieldPolynomialsCalculator.convertByteToInt(reversed);
            for (int k = 0; k < 4; k++) {
                int shifted = GaloisFieldPolynomialsCalculator.convertByteToInt(madeLeftCycleShit(reversed, k+1));
                toMakeAffineTransform ^= shifted;
            }
            sBox[i] = GaloisFieldPolynomialsCalculator.convertIntToByte (toMakeAffineTransform ^ 99);
        }
        return sBox;
    }
    private static byte madeLeftCycleShit(byte b, int shift) {
        int translatedB = GaloisFieldPolynomialsCalculator.convertByteToInt(b);
        int shifted = (translatedB << shift) | (translatedB >> (Byte.SIZE - shift));
        return GaloisFieldPolynomialsCalculator.convertIntToByte(shifted);
    }

    private final RoundKeysGenerator roundKeysGenerator;
    private final RoundTransformer roundTransformer;
    private final int irreduciblePolynomial;
    private byte[] cipherKey;

    public Rijndael(RoundKeysGenerator roundKeysGenerator, RoundTransformer roundTransformer, int irreduciblePolynomial) {
        this.roundKeysGenerator = roundKeysGenerator;
        this.roundTransformer = roundTransformer;
        this.irreduciblePolynomial = irreduciblePolynomial;
    }

    public Rijndael(RoundKeysGenerator roundKeysGenerator, RoundTransformer roundTransformer) {
        this.roundKeysGenerator = roundKeysGenerator;
        this.roundTransformer = roundTransformer;
        this.irreduciblePolynomial = 283;
    }

    @Override
    public byte[] decode(byte[] inputBlock) {
        return new byte[0];
    }

    @Override
    public byte[] encode(byte[] inputBlock) {
        return new byte[0];
    }

    @Override
    public void setKey(byte[] cipherKey) {
        this.cipherKey = cipherKey;
    }
}
