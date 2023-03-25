package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 2^8 Galois field calculator
 **/
public class GaloisFieldPolynomialsCalculator {
    private static Set<Integer> irreduciblePolynomials = new HashSet<>();

    public static GaloisFieldPolynomialsCalculator getInstance(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (!polynomialIsIrreducible(irreduciblePolynomial)) {
            throw new WrongIrreduciblePolynomialException();
        }
        return new GaloisFieldPolynomialsCalculator(irreduciblePolynomial);
    }
    private static boolean polynomialIsIrreducible(int polynomial) {
        if (polynomial < 2) {
            return false;
        }

        int i = 2;
        int polynomialDegree = getDegree(polynomial);
        while (polynomialDegree >= 2 * getDegree(i)) {
            if (mod(polynomial, i++) == 0) {
                return false;
            }
        }
        return true;
    }

    private final int irreduciblePolynomial;
    protected GaloisFieldPolynomialsCalculator(int irreduciblePolynomial) {
        this.irreduciblePolynomial = irreduciblePolynomial;
    }

    public byte sum(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial ^ secondPolynomial);
    }

    public List<Integer> getAllIrreduciblePolynomials() {
        int degree = 8;
        final int twoDegree = degree;
        final int POLYNOMIAL_NUMBER = 2 << degree;

        return IntStream
                .range(1 << twoDegree, POLYNOMIAL_NUMBER)
                .filter(GaloisFieldPolynomialsCalculator::polynomialIsIrreducible)
                .boxed()
                .toList();
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
    static byte mod(int firstPolynomial, int secondPolynomial) {
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

    public byte getReverse(byte polynomial) {
        if (polynomial == 0) {
            return 0;
        }

        return 0;
    }

    public static String polynomialToString(byte polynomial) {
        return String.format("%8s", Integer.toBinaryString(polynomial & 0xFF)).replace(' ', '0');
    }
    public static String polynomialToString(int polynomial) {
        return Integer.toBinaryString(polynomial);
    }

}
