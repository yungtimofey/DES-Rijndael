package com.company.crypto.algorithm.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculatorImpl;
import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

import java.util.HashMap;

public final class Rijndael implements SymmetricalBlockEncryptionAlgorithm {
    public enum RijndaelBlockSize {
        BIT_128(128), BIT_196(196), BIT_256(256);

        public final int bitsNumber;
        RijndaelBlockSize(int bitsNumber) {
            this.bitsNumber = bitsNumber;
        }
    }


    private static final HashMap<Integer, byte[]> sBoxAndItsIrreduciblePolynomial = new HashMap<>();
    private static final GaloisFieldPolynomialsCalculator galoisFieldPolynomialsCalculator = new GaloisFieldPolynomialsCalculatorImpl();

    public static byte[] getSBox(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (!sBoxAndItsIrreduciblePolynomial.containsKey(irreduciblePolynomial)) {
            sBoxAndItsIrreduciblePolynomial.put(irreduciblePolynomial, generateSBox(irreduciblePolynomial));
        }

        // TODO: make copy
        return sBoxAndItsIrreduciblePolynomial.get(irreduciblePolynomial);
    }
    private static byte[] generateSBox(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        final int sBoxSize = 16;

        byte[] sBox = new byte[sBoxSize * sBoxSize];
        for (int i = 0; i < sBox.length; i++) {
            byte toReverse = GaloisFieldPolynomialsCalculator.convertIntToByte(i);
            byte reversed = galoisFieldPolynomialsCalculator.getReverse(toReverse, irreduciblePolynomial);

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

    private static byte[][] RCON = null;
    public static byte[][] getRCON(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (RCON == null) {
            final int maxNumberOfRounds = 14;
            byte[] rc = new byte[maxNumberOfRounds];

            rc[0] = 0;
            for (int i = 1; i < rc.length; i++) {
                rc[i] = galoisFieldPolynomialsCalculator.multi(rc[i-1], (byte) 2, irreduciblePolynomial);
            }

            RCON = new byte[maxNumberOfRounds][4];
            for (int i = 0; i < rc.length; i++) {
                RCON[0][i] = rc[i];
            }
        }

        // TODO: make copy
        return RCON;
    }


    private final RoundKeysGenerator roundKeysGenerator;
    private final RoundTransformer roundTransformer;
    private final int irreduciblePolynomial;
    private byte[] cipherKey;

   // TODO: made static method for init polynomial

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
