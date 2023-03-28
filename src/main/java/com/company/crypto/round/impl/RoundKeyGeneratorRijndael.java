package com.company.crypto.round.impl;

import com.company.crypto.algorithm.impl.Rijndael;
import com.company.crypto.round.RoundKeysGenerator;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculatorImpl;
import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

import java.nio.file.Path;

public final class RoundKeyGeneratorRijndael implements RoundKeysGenerator {
    private final int openTextColumnNumber;
    private final int keyColumnNumber;
    private final int roundNumber;
    final byte[][] W;
    private final int irreduciblePolynomial;
    private static final int ROW_NUMBER = 4;

    // TODO: make polynomial check
    public RoundKeyGeneratorRijndael(
            Rijndael.RijndaelBlockSize openTextSize,
            Rijndael.RijndaelBlockSize cipherKeySize,
            int irreduciblePolynomial) {

        this.openTextColumnNumber = openTextSize.bitsNumber / (ROW_NUMBER * Byte.SIZE);
        this.keyColumnNumber = cipherKeySize.bitsNumber / (ROW_NUMBER * Byte.SIZE);
        this.roundNumber = Rijndael.getRoundNumber(openTextSize, cipherKeySize);
        this.W = new byte[openTextColumnNumber * (roundNumber + 1)][ROW_NUMBER];

        this.irreduciblePolynomial = irreduciblePolynomial;
    }

    @Override
    public byte[][] generate(byte[] cipherKey) {
        byte[] tmpArray = new byte[ROW_NUMBER];

        if (keyColumnNumber <= 6) {
            setCipherKeyInW(cipherKey);
            for (int i = keyColumnNumber; i < W.length; i += keyColumnNumber) {
                System.arraycopy(W[i - 1], 0, tmpArray, 0, ROW_NUMBER);
                rotateArrayLeftOnce(tmpArray);

                try {
                    Rijndael.subByte(tmpArray, irreduciblePolynomial);
                    XOR(W[i - keyColumnNumber], tmpArray, W[i]);

                    byte[][] CORN = Rijndael.getRCON(irreduciblePolynomial);
                    byte[] currentCORN = CORN[i / keyColumnNumber - 1];
                    XOR(W[i], currentCORN, W[i]);
                } catch (WrongIrreduciblePolynomialException e) {
                    throw new IllegalStateException("Wrong polynomial:" + irreduciblePolynomial);
                }

                for (int j = 1; j < keyColumnNumber; j++) {
                    XOR(W[i + j - keyColumnNumber], W[i + j - 1], W[i + j]);
                }
            }
        } else {

        }

        byte[] roundKey = new byte[ROW_NUMBER * openTextColumnNumber];
        byte[][] roundKeys = new byte[roundNumber][ROW_NUMBER * openTextColumnNumber];

        int numberOFCopiedKeys = 0;
        int currentPositionForCopy = 0;
        for (int i = keyColumnNumber; i < W.length; i++) {
            byte[] currentW = W[i];

            System.arraycopy(currentW, 0, roundKey, currentPositionForCopy, ROW_NUMBER);
            currentPositionForCopy += ROW_NUMBER;

            if (currentPositionForCopy == roundKey.length) {
                System.arraycopy(roundKey, 0, roundKeys[numberOFCopiedKeys], 0, roundKey.length);
                currentPositionForCopy = 0;
                numberOFCopiedKeys++;
            }
        }
        return roundKeys;
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
}
