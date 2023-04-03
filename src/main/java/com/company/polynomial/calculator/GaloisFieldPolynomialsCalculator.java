package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;
import lombok.AllArgsConstructor;

import java.util.List;

public interface GaloisFieldPolynomialsCalculator {
    @AllArgsConstructor
    class EEATuple {
        int d;
        int x;
        int y;
    }

    byte sum(byte... polynomials);

    byte multi(byte firstPolynomial, byte secondPolynomial, int irreduciblePolynomial) throws WrongIrreduciblePolynomialException;

    List<Integer> getAllIrreduciblePolynomials();

    byte getReverse(byte polynomial, int irreduciblePolynomial) throws WrongIrreduciblePolynomialException;

    static byte convertIntToByte(int polynomial) {
        byte converted = 0;
        converted |= polynomial;
        return converted;
    }

    static int convertByteToInt(byte polynomial) {
        return (polynomial & 0xFF);
    }

    static String polynomialToString(byte polynomial) {
        return String.format("%8s", Integer.toBinaryString(polynomial & 0xFF)).replace(" ", "");
    }

    static String polynomialToString(int polynomial) {
        return Integer.toBinaryString(polynomial);
    }
}
