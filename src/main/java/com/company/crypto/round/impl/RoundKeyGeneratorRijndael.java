package com.company.crypto.round.impl;

import com.company.crypto.algorithm.impl.Rijndael;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

public final class RoundKeyGeneratorRijndael implements RoundKeysGenerator {
    private final int openTextColumnNumber;
    private final int keyColumnNumber;
    private final int roundNumber;
    final byte[][] W;
    private final int irreduciblePolynomial;
    private static final int ROW_NUMBER = 4;

    private final Rijndael.RijndaelBlockSize openTextSize;
    private final Rijndael.RijndaelBlockSize cipherKeySize;

    public RoundKeyGeneratorRijndael(
            int irreduciblePolynomial,
            Rijndael.RijndaelBlockSize openTextSize,
            Rijndael.RijndaelBlockSize cipherKeySize
    ) {
        this.openTextColumnNumber = openTextSize.bitsNumber / (ROW_NUMBER * Byte.SIZE);
        this.keyColumnNumber = cipherKeySize.bitsNumber / (ROW_NUMBER * Byte.SIZE);
        this.roundNumber = Rijndael.getRoundNumber(openTextSize, cipherKeySize);
        this.W = new byte[openTextColumnNumber * (roundNumber + 1)][ROW_NUMBER];

        this.irreduciblePolynomial = irreduciblePolynomial;

        this.openTextSize = openTextSize;
        this.cipherKeySize = cipherKeySize;
    }

    @Override
    public byte[][] generate(byte[] cipherKey) {
        try {
            return tryToGenerate(cipherKey);
        } catch (WrongIrreduciblePolynomialException e) {
            throw new IllegalStateException("Wrong polynomial:" + irreduciblePolynomial);
        }
    }
    private byte[][] tryToGenerate(byte[] cipherKey) throws WrongIrreduciblePolynomialException {
        byte[] tmpArray = new byte[ROW_NUMBER];

        setCipherKeyInW(cipherKey);
        int i = keyColumnNumber;

        byte[][] RCON = Rijndael.getRCON(irreduciblePolynomial);
        while (i < openTextColumnNumber * (roundNumber + 1)) {
            System.arraycopy(W[i - 1], 0, tmpArray, 0, ROW_NUMBER);
            if (i % keyColumnNumber == 0) {
                rotateArrayLeftOnce(tmpArray);
                Rijndael.subByte(tmpArray, irreduciblePolynomial);
                XOR(tmpArray, RCON[i/keyColumnNumber - 1], tmpArray);
            } else if (keyColumnNumber > 6 && i % keyColumnNumber == 4) {
                Rijndael.subByte(tmpArray, irreduciblePolynomial);
            }

            XOR(W[i - keyColumnNumber], tmpArray, W[i]);
            i++;
        }
        return getRoundKeysFromW();
    }

    private void setCipherKeyInW(byte[] cipherKey) {
        for (int i = 0; i < keyColumnNumber; i++) {
            System.arraycopy(cipherKey, i * ROW_NUMBER, W[i], 0, ROW_NUMBER);
        }
    }

    private void rotateArrayLeftOnce(byte[] array) {
        byte first = array[0];
        for (int i = 1; i < array.length; i++) {
            array[i - 1] = array[i];
        }
        array[array.length - 1] = first;
    }

    private void XOR(byte[] firstArray, byte[] secondArray, byte[] outArray) {
        for (int i = 0; i < firstArray.length; i++) {
            int firstInt = GaloisFieldPolynomialsCalculator.convertByteToInt(firstArray[i]);
            int secondInt = GaloisFieldPolynomialsCalculator.convertByteToInt(secondArray[i]);
            outArray[i] = GaloisFieldPolynomialsCalculator.convertIntToByte(firstInt ^ secondInt);
        }
    }

    private byte[][] getRoundKeysFromW() {
        final int openTextBytes = ROW_NUMBER * openTextColumnNumber;

        byte[] roundKey = new byte[ROW_NUMBER * openTextColumnNumber];
        byte[][] roundKeys = new byte[roundNumber][ROW_NUMBER * openTextColumnNumber];

        int numberOFCopiedKeys = 0;
        int currentPositionForCopy = 0;
        for (int i = openTextColumnNumber; i < W.length && numberOFCopiedKeys < roundNumber; i++) {
            byte[] currentW = W[i];

            System.arraycopy(currentW, 0, roundKey, currentPositionForCopy, ROW_NUMBER);
            currentPositionForCopy += ROW_NUMBER;

            if (currentPositionForCopy == openTextBytes) {
                System.arraycopy(roundKey, 0, roundKeys[numberOFCopiedKeys], 0, roundKey.length);
                currentPositionForCopy = 0;
                numberOFCopiedKeys++;
            }
        }
        return roundKeys;
    }

    public int getIrreduciblePolynomial() {
        return irreduciblePolynomial;
    }

    public Rijndael.RijndaelBlockSize getOpenTextSize() {
        return openTextSize;
    }

    public Rijndael.RijndaelBlockSize getCipherKeySize() {
        return cipherKeySize;
    }
}
