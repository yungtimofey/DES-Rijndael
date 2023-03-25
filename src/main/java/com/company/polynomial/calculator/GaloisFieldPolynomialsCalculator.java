package com.company.polynomial.calculator;

import lombok.AllArgsConstructor;

import java.util.List;

public interface GaloisFieldPolynomialsCalculator {
    @AllArgsConstructor
    class EEATuple {
        int d;
        int x;
        int y;
    }

    byte sum(byte firstPolynomial, byte secondPolynomial);

    byte minus(byte firstPolynomial, byte secondPolynomial);

    byte multi(byte firstPolynomial, byte secondPolynomial);

    List<Integer> getAllIrreduciblePolynomials();

    byte getReverse(byte polynomial);


    static byte convertIntToByte(int polynomial) {
        byte converted = 0;
        for (int i = 0; i < Byte.SIZE; i++) {
            converted |= polynomial & (1 << i);
        }
        return converted;
    }

    static int convertByteToInt(byte polynomial) {
        int converted = 0;
        for (int i = 0; i < Byte.SIZE; i++) {
            converted |= polynomial & (1 << i);
        }
        return converted;
    }

    static String polynomialToString(byte polynomial) {
        return String.format("%8s", Integer.toBinaryString(polynomial & 0xFF)).replace(" ", "");
    }

    static String polynomialToString(int polynomial) {
        return Integer.toBinaryString(polynomial);
    }
}
