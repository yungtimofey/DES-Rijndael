package com.company.crypto.round.impl;

import com.company.crypto.algorithm.impl.Rijndael;
import com.company.crypto.round.RoundTransformer;
import com.company.polynomial.calculator.GaloisFieldPolynomialsCalculator;
import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

import java.util.Arrays;

public class RoundTransformerRijndael implements RoundTransformer {
    private static final int COLUMN_NUMBER = 4;

    private final int irreduciblePolynomial;
    private final byte[][] inputBlockTransformed;
    private final byte[][] roundKeyTransformed;
    private final int rowNumber;
    private final byte[] arrayForShift;
    private final GaloisFieldPolynomialsCalculator calculator;

    public RoundTransformerRijndael(
            int irreduciblePolynomial,
            Rijndael.RijndaelBlockSize openTextSize,
            GaloisFieldPolynomialsCalculator galoisFieldPolynomialsCalculator
    ) {
        this.irreduciblePolynomial = irreduciblePolynomial;
        this.rowNumber = openTextSize.bitsNumber / COLUMN_NUMBER / 8;
        this.calculator = galoisFieldPolynomialsCalculator;

        this.inputBlockTransformed = new byte[rowNumber][COLUMN_NUMBER];
        this.roundKeyTransformed = new byte[rowNumber][COLUMN_NUMBER];
        this.arrayForShift = new byte[rowNumber];
    }

    @Override
    public byte[] encode(byte[] inputBlock, byte[] roundKey, boolean predicate) {
        try {
            return tryToEncode(inputBlock, roundKey, predicate);
        } catch (WrongIrreduciblePolynomialException e) {
            throw new IllegalStateException("Wrong Polynomial:" + irreduciblePolynomial);
        }
    }

    private byte[] tryToEncode(byte[] inputBlock, byte[] roundKey, boolean isLastRound) throws WrongIrreduciblePolynomialException {
        transformArray(inputBlock, inputBlockTransformed);
        transformArray(roundKey, roundKeyTransformed);

        Rijndael.subByte(inputBlock, irreduciblePolynomial);
        shiftRows(inputBlockTransformed);
        if (!isLastRound) {
            mixColumns(inputBlockTransformed);
        }
        addRoundKey(inputBlockTransformed, roundKeyTransformed);
        return inputBlock;
    }

    private void transformArray(byte[] inputBlock, byte[][] inputBlockTransformed) {
        int numberOfSavedTransformedInputBlocks = 0;
        for (int i = 0; i < inputBlock.length; i++) {
            inputBlockTransformed[numberOfSavedTransformedInputBlocks][i % COLUMN_NUMBER] = inputBlock[i];
            if (i != 0 && (i+1) % COLUMN_NUMBER == 0) {
                numberOfSavedTransformedInputBlocks++;
            }
        }
    }

    private void shiftRows(byte[][] transformedInputBlock) {
        for (int i = 0; i < COLUMN_NUMBER; i++) {
            for (int j = 0; j < rowNumber; j++) {
                arrayForShift[j] = transformedInputBlock[j][i];
            }

            shiftArrayLeft(arrayForShift, i);

            for (int j = 0; j < rowNumber; j++) {
                transformedInputBlock[j][i] = arrayForShift[j];
            }
        }
    }

    private void shiftArrayLeft(byte[] array, int shiftNumber) {
        for (int i = 0; i < shiftNumber; i++) {
            rotateArrayLeftOnce(array);
        }
    }

    private void rotateArrayLeftOnce(byte[] array) {
        byte first = array[0];
        for (int i = 1; i < array.length; i++) {
            array[i - 1] = array[i];
        }
        array[array.length - 1] = first;
    }

    private void mixColumns(byte[][] inputBlockTransformed) throws WrongIrreduciblePolynomialException {
        for (byte[] column : inputBlockTransformed) {
            byte firstMixed = calculator.sum(
                    calculator.multi((byte) 2, column[0], irreduciblePolynomial),
                    calculator.multi((byte) 3, column[1], irreduciblePolynomial),
                    column[2],
                    column[3]
            );

            byte secondMixed = calculator.sum(
                    column[0],
                    calculator.multi((byte) 2, column[1], irreduciblePolynomial),
                    calculator.multi((byte) 3, column[2], irreduciblePolynomial),
                    column[3]
            );

            byte thirdMixed = calculator.sum(
                    column[0],
                    column[1],
                    calculator.multi((byte) 2, column[2], irreduciblePolynomial),
                    calculator.multi((byte) 3, column[3], irreduciblePolynomial)
            );

            byte fourMixed = calculator.sum(
                    calculator.multi((byte) 3, column[0], irreduciblePolynomial),
                    column[1],
                    column[2],
                    calculator.multi((byte) 2, column[3], irreduciblePolynomial)
            );

            column[0] = firstMixed;
            column[1] = secondMixed;
            column[2] = thirdMixed;
            column[3] = fourMixed;
        }
    }

    private void addRoundKey(byte[][] inputBlockTransformed, byte[][] roundKeyTransformed) {
        for (int i = 0; i < inputBlockTransformed.length; i++) {
            XOR(inputBlockTransformed[i], roundKeyTransformed[i], inputBlockTransformed[i]);
        }
    }

    private void XOR(byte[] firstArray, byte[] secondArray, byte[] outArray) {
        for (int i = 0; i < firstArray.length; i++) {
            int firstInt = GaloisFieldPolynomialsCalculator.convertByteToInt(firstArray[i]);
            int secondInt = GaloisFieldPolynomialsCalculator.convertByteToInt(secondArray[i]);
            outArray[i] = GaloisFieldPolynomialsCalculator.convertIntToByte(firstInt ^ secondInt);
        }
    }

    @Override
    public byte[] decode(byte[] inputBlock, byte[] roundKey, boolean isFirstRound) {
        try {
            return tryToDecode(inputBlock, roundKey, isFirstRound);
        } catch (WrongIrreduciblePolynomialException e) {
            throw new IllegalStateException("Wrong Polynomial:" + irreduciblePolynomial);
        }
    }
    private byte[] tryToDecode(byte[] inputBlock, byte[] roundKey, boolean isFirstRound) throws WrongIrreduciblePolynomialException {
        transformArray(inputBlock, inputBlockTransformed);
        transformArray(roundKey, roundKeyTransformed);

        addRoundKey(inputBlockTransformed, roundKeyTransformed);
        if (!isFirstRound) {
            inverseMixColumns(inputBlockTransformed);
        }
        inverseShiftRows(inputBlockTransformed);
        Rijndael.inverseSubByte(inputBlock, irreduciblePolynomial);

        return inputBlock;
    }
    private void inverseMixColumns(byte[][] inputBlockTransformed) throws WrongIrreduciblePolynomialException {
        for (byte[] column : inputBlockTransformed) {
            int e = 14;
            int b = 11;
            int d = 13;

            byte firstMixed = calculator.sum(
                    calculator.multi((byte) e, column[0], irreduciblePolynomial),
                    calculator.multi((byte) b, column[1], irreduciblePolynomial),
                    calculator.multi((byte) d, column[2], irreduciblePolynomial),
                    calculator.multi((byte) 9, column[3], irreduciblePolynomial)
            );

            byte secondMixed = calculator.sum(
                    calculator.multi((byte) 9, column[0], irreduciblePolynomial),
                    calculator.multi((byte) e, column[1], irreduciblePolynomial),
                    calculator.multi((byte) b, column[2], irreduciblePolynomial),
                    calculator.multi((byte) d, column[3], irreduciblePolynomial)
            );

            byte thirdMixed = calculator.sum(
                    calculator.multi((byte) d, column[0], irreduciblePolynomial),
                    calculator.multi((byte) 9, column[1], irreduciblePolynomial),
                    calculator.multi((byte) e, column[2], irreduciblePolynomial),
                    calculator.multi((byte) b, column[3], irreduciblePolynomial)
            );

            byte fourMixed = calculator.sum(
                    calculator.multi((byte) b, column[0], irreduciblePolynomial),
                    calculator.multi((byte) d, column[1], irreduciblePolynomial),
                    calculator.multi((byte) 9, column[2], irreduciblePolynomial),
                    calculator.multi((byte) e, column[3], irreduciblePolynomial)
            );

            column[0] = firstMixed;
            column[1] = secondMixed;
            column[2] = thirdMixed;
            column[3] = fourMixed;
        }
    }

    private void inverseShiftRows(byte[][] transformedInputBlock) {
        for (int i = 0; i < COLUMN_NUMBER; i++) {
            for (int j = 0; j < rowNumber; j++) {
                arrayForShift[j] = transformedInputBlock[j][i];
            }

            shiftArrayRight(arrayForShift, i);

            for (int j = 0; j < rowNumber; j++) {
                transformedInputBlock[j][i] = arrayForShift[j];
            }
        }
    }
    private void shiftArrayRight(byte[] array, int shiftNumber) {
        for (int i = 0; i < shiftNumber; i++) {
            rotateArrayRightOnce(array);
        }
    }
    private void rotateArrayRightOnce(byte[] array) {
        byte last = array[array.length - 1];
        for (int i = 1; i < array.length; i++) {
            array[i] = array[i-1];
        }
        array[0] = last;
    }


}
