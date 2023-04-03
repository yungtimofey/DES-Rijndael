package com.company.crypto.algorithm.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculatorImpl;
import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

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
    private static final HashMap<Integer, byte[]> inverseSBoxAndItsIrreduciblePolynomial = new HashMap<>();
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
                int shifted = GaloisFieldPolynomialsCalculator.convertByteToInt(madeLeftCycleShit(reversed, k + 1));
                toMakeAffineTransform ^= shifted;
            }
            sBox[i] = GaloisFieldPolynomialsCalculator.convertIntToByte(toMakeAffineTransform ^ 99);
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
            final int RCONSize = 256;
            byte[] rc = new byte[RCONSize];

            rc[0] = 1;
            for (int i = 1; i < rc.length; i++) {
                rc[i] = galoisFieldPolynomialsCalculator.multi(rc[i - 1], (byte) 2, irreduciblePolynomial);
            }

            RCON = new byte[RCONSize][4];
            for (int i = 0; i < rc.length; i++) {
                RCON[i][0] = rc[i];
            }
        }

        // TODO: make copy
        return RCON;
    }

    public static void subByte(byte[] array, int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        byte[] sBox = getSBox(irreduciblePolynomial);
        char[] hexArrayForSBlock = new char[2];
        for (int i = 0; i < array.length; i++) {
            int row = getRow(array[i], hexArrayForSBlock);
            int column = getColumn(array[i], hexArrayForSBlock);
            array[i] = sBox[row * S_BOX_SIZE + column];
        }
    }

    private static int getRow(byte digit, char[] hexArrayForSBlock) {
        int intDigit = GaloisFieldPolynomialsCalculator.convertByteToInt(digit);
        toHexArray(intDigit, hexArrayForSBlock);

        if (hexArrayForSBlock[1] == 0) {
            return 0;
        }

        char c = hexArrayForSBlock[0];
        return Character.isDigit(c) ? c - '0' : c - 'a' + 10;
    }

    private static int getColumn(byte digit, char[] hexArrayForSBlock) {
        int intDigit = GaloisFieldPolynomialsCalculator.convertByteToInt(digit);
        toHexArray(intDigit, hexArrayForSBlock);

        char c;
        if (hexArrayForSBlock[1] == 0) {
            c = hexArrayForSBlock[0];
        } else {
            c = hexArrayForSBlock[1];
        }
        return Character.isDigit(c) ? c - '0' : c - 'a' + 10;
    }

    private static void toHexArray(int digit, char[] hexArrayForSBlock) {
        hexArrayForSBlock[1] = (char) (
                (digit % 16) < 10
                        ? digit % 16 + '0'
                        : digit % 16 + 'a' - 10);
        hexArrayForSBlock[0] = (char) (
                (digit / 16) < 10
                        ? digit / 16 + '0'
                        : digit / 16 + 'a' - 10);
    }

    public static void inverseSubByte(byte[] array, int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        char[] hexArrayForSBlock = new char[2];
        byte[] inverseSBox = getInverseSBox(irreduciblePolynomial);
        for (int i = 0; i < array.length; i++) {
            int row = getRow(array[i], hexArrayForSBlock);
            int column = getColumn(array[i], hexArrayForSBlock);
            array[i] = inverseSBox[row * S_BOX_SIZE + column];
        }
    }

    public static byte[] getInverseSBox(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (!inverseSBoxAndItsIrreduciblePolynomial.containsKey(irreduciblePolynomial)) {
            inverseSBoxAndItsIrreduciblePolynomial.put(irreduciblePolynomial, generateInverseSBox(irreduciblePolynomial));
        }
        return inverseSBoxAndItsIrreduciblePolynomial.get(irreduciblePolynomial);
    }

    private static byte[] generateInverseSBox(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        byte[] inverseSBox = new byte[S_BOX_SIZE * S_BOX_SIZE];
        byte[] sBox = getSBox(irreduciblePolynomial);
        char[] hexArrayForSBlock = new char[2];

        for (int i = 0; i < inverseSBox.length; i++) {
            int row = getRow(sBox[i], hexArrayForSBlock);
            int column = getColumn(sBox[i], hexArrayForSBlock);

            byte iInt = GaloisFieldPolynomialsCalculator.convertIntToByte(i);
            int toSet = getRow(iInt, hexArrayForSBlock) * S_BOX_SIZE + getColumn(iInt, hexArrayForSBlock);
            inverseSBox[row * S_BOX_SIZE + column] = GaloisFieldPolynomialsCalculator.convertIntToByte(toSet);
        }
        return inverseSBox;
    }


    public static int getRoundNumber(Rijndael.RijndaelBlockSize openTextSize, Rijndael.RijndaelBlockSize cipherKeySize) {
        int openTextBitsNumber = openTextSize.bitsNumber;
        int cipherKeyBitsNumber = cipherKeySize.bitsNumber;

        if (openTextBitsNumber == 256 || cipherKeyBitsNumber == 256) {
            return 14;
        }
        if (openTextBitsNumber == 128 && cipherKeyBitsNumber == 128) {
            return 10;
        }
        return 12;
    }

    private final RoundKeysGenerator roundKeysGenerator;
    private final RoundTransformer roundTransformer;
    private final int roundNumber;
    private final int openTextBlockSizeInBytes;
    private byte[] cipherKey;

    public Rijndael(
            RoundKeysGenerator roundKeysGenerator,
            RoundTransformer roundTransformer,
            Rijndael.RijndaelBlockSize openTextSize,
            Rijndael.RijndaelBlockSize cipherKeySize
    ) {
        this.roundKeysGenerator = roundKeysGenerator;
        this.roundTransformer = roundTransformer;
        this.roundNumber = Rijndael.getRoundNumber(openTextSize, cipherKeySize);
        this.openTextBlockSizeInBytes = openTextSize.bitsNumber / Byte.SIZE;
    }

    @Override
    public byte[] decode(byte[] inputBlock) {
        Objects.requireNonNull(inputBlock);
        Objects.requireNonNull(cipherKey);

        byte[][] roundKeys = roundKeysGenerator.generate(cipherKey);
        for (int i = 0; i < roundNumber; i++) {
            inputBlock = roundTransformer.encode(Arrays.copyOf(inputBlock, inputBlock.length), roundKeys[i], i == roundNumber - 1);
        }
        return inputBlock;
    }

    @Override
    public byte[] encode(byte[] inputBlock) {
        Objects.requireNonNull(inputBlock);
        Objects.requireNonNull(cipherKey);

        byte[][] roundKeys = roundKeysGenerator.generate(cipherKey);
        for (int i = 0; i < roundNumber; i++) {
            inputBlock = roundTransformer.decode(Arrays.copyOf(inputBlock, inputBlock.length), roundKeys[i], i == roundNumber - 1);
        }
        return inputBlock;
    }

    @Override
    public void setKey(byte[] cipherKey) {
        this.cipherKey = cipherKey;
    }

    @Override
    public int getOpenTextBlockSizeInBytes() {
        return openTextBlockSizeInBytes;
    }
}
