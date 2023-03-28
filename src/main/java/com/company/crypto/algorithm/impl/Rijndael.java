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
        BIT_128(128), BIT_192(192), BIT_256(256);

        public final int bitsNumber;
        RijndaelBlockSize(int bitsNumber) {
            this.bitsNumber = bitsNumber;
        }
    }

    private static final int S_BOX_SIZE = 16;
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
        byte[] sBox = new byte[S_BOX_SIZE * S_BOX_SIZE];
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
            final int maxNumberOfRounds = 16;
            byte[] rc = new byte[maxNumberOfRounds];

            rc[0] = 1;
            for (int i = 1; i < rc.length; i++) {
                rc[i] = galoisFieldPolynomialsCalculator.multi(rc[i-1], (byte)2, irreduciblePolynomial);
            }

            RCON = new byte[maxNumberOfRounds][4];
            for (int i = 0; i < rc.length; i++) {
                RCON[i][0] = rc[i];
            }
        }

        // TODO: make copy
        return RCON;
    }

    public static void subByte(byte[] array, int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        byte[] sBox = getSBox(irreduciblePolynomial);
        for (int i = 0; i < array.length; i++) {
            int row = getRow(array[i]);
            int column = getColumn(array[i]);
            array[i] = sBox[row * S_BOX_SIZE + column];
        }
    }
    private static int getRow(byte digit) {
        int intDigit = GaloisFieldPolynomialsCalculator.convertByteToInt(digit);
        String hexDigit = Integer.toHexString(intDigit);

        if (hexDigit.length() == 1) {
            return 0;
        }

        char c = Integer.toHexString(intDigit).charAt(0);
        return Character.isDigit(c) ? c - '0' : c - 'a' + 10;
    }
    private static int getColumn(byte digit) {
        int intDigit = GaloisFieldPolynomialsCalculator.convertByteToInt(digit);
        String hexDigit = Integer.toHexString(intDigit);

        char c;
        if (hexDigit.length() == 1) {
            c = Integer.toHexString(intDigit).charAt(0);
        } else {
            c = Integer.toHexString(intDigit).charAt(1);
        }
        return Character.isDigit(c) ? c - '0' : c - 'a' + 10;
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
