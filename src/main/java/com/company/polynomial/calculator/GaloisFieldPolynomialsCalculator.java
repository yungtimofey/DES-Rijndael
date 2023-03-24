package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

/**
 * 2^8 Galois field calculator
 **/
public class GaloisFieldPolynomialsCalculator {
    public static GaloisFieldPolynomialsCalculator getInstance(int irreduciblePolynomial)
            throws WrongIrreduciblePolynomialException {

        if (polynomialIsNotIrreducible(irreduciblePolynomial)) {
            throw new WrongIrreduciblePolynomialException();
        }
        return new GaloisFieldPolynomialsCalculator(irreduciblePolynomial);
    }
    private static boolean polynomialIsNotIrreducible(int irreduciblePolynomial) {
        return false;
    }

    private final int irreduciblePolynomial;
    protected GaloisFieldPolynomialsCalculator(int irreduciblePolynomial) {
        this.irreduciblePolynomial = irreduciblePolynomial;
    }

    public byte sum(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial ^ secondPolynomial);
    }

    public byte multi(byte firstPolynomial, byte secondPolynomial) {
        int multipliedGalyaPolynomial = multiPolynomials(firstPolynomial, secondPolynomial);
        return mod(multipliedGalyaPolynomial, irreduciblePolynomial);
    }
    private int multiPolynomials(byte firstPolynomial, byte secondPolynomial) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            result ^= firstPolynomial * (secondPolynomial & (1 << i));
        }
        return result;
    }
    private byte mod(int firstPolynomial, int secondPolynomial) {
        int degreeOfFirstPolynomial = getDegree(firstPolynomial);
        int degreeOfSecondPolynomial = getDegree(secondPolynomial);

        while (degreeOfFirstPolynomial >= degreeOfSecondPolynomial) {
            int deltaDegree = degreeOfFirstPolynomial - degreeOfSecondPolynomial;
            int polynomialToMinus = secondPolynomial << deltaDegree;

            firstPolynomial = firstPolynomial ^ polynomialToMinus;

            degreeOfFirstPolynomial = getDegree(firstPolynomial);
        }
        return (byte) firstPolynomial;
    }
    private static int getDegree(int polynomial) {
        if (polynomial == 0) return 0;

        int degree = 0;
        while (polynomial != 0) {
            degree++;
            polynomial = polynomial >> 1;
        }
        return degree-1;
    }

    public static String polynomialToString(byte polynomial) {
        return String.format("%8s", Integer.toBinaryString(polynomial & 0xFF)).replace(' ', '0');
    }

}
