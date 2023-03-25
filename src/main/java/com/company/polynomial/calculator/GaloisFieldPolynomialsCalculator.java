package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 2^8 Galois field calculator
 **/
public final class GaloisFieldPolynomialsCalculator {
    public static GaloisFieldPolynomialsCalculator getInstance(int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (!polynomialIsIrreducible(irreduciblePolynomial)) {
            throw new WrongIrreduciblePolynomialException();
        }
        return new GaloisFieldPolynomialsCalculator(irreduciblePolynomial);
    }

    static boolean polynomialIsIrreducible(int polynomial) {
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

    static byte mod(int firstPolynomial, int secondPolynomial) {
        if (secondPolynomial == 1) {
            return 0;
        }

        int degreeOfFirstPolynomial = getDegree(firstPolynomial);
        int degreeOfSecondPolynomial = getDegree(secondPolynomial);

        while (degreeOfFirstPolynomial >= degreeOfSecondPolynomial && degreeOfFirstPolynomial != 0) {
            int deltaDegree = degreeOfFirstPolynomial - degreeOfSecondPolynomial;
            int polynomialToMinus = secondPolynomial << deltaDegree;

            firstPolynomial = firstPolynomial ^ polynomialToMinus;

            degreeOfFirstPolynomial = getDegree(firstPolynomial);
        }
        return (byte) firstPolynomial;
    }

    static int getDegree(int polynomial) {
        if (polynomial == 0) return 0;

        int degree = 0;
        while (polynomial != 0 && polynomial != -1) {
            degree++;
            polynomial = polynomial >> 1;
        }
        return degree - 1;
    }


    private final int irreduciblePolynomial;
    private final Set<Integer> allIrreduciblePolynomials = new HashSet<>();

    private GaloisFieldPolynomialsCalculator(int irreduciblePolynomial) {
        this.irreduciblePolynomial = irreduciblePolynomial;
        initIrreduciblePolynomialsSet();
    }

    private void initIrreduciblePolynomialsSet() {
        final int twoDegreeOfGaloisField = 8;
        IntStream
                .range(1 << twoDegreeOfGaloisField, 1 << (twoDegreeOfGaloisField + 1))
                .filter(GaloisFieldPolynomialsCalculator::polynomialIsIrreducible)
                .forEach(allIrreduciblePolynomials::add);
    }


    public byte sum(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial ^ secondPolynomial);
    }

    public byte minus(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial ^ secondPolynomial);
    }

    public byte multi(byte firstPolynomial, byte secondPolynomial) {
        int multipliedGalyaPolynomial = multiPolynomials(firstPolynomial, secondPolynomial);
        return mod(multipliedGalyaPolynomial, irreduciblePolynomial);
    }

    int multiPolynomials(byte firstPolynomial, byte secondPolynomial) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            result ^= firstPolynomial * (secondPolynomial & (1 << i));
        }
        return result;
    }


    @AllArgsConstructor
    static class EEATuple {
        int d;
        int x;
        int y;
    }

    public byte getReverse(byte polynomial) {
        if (polynomial == 0) {
            return 0;
        }
        EEATuple eea = EEA(irreduciblePolynomial, polynomial);
        return (byte) eea.x;
    }

    EEATuple EEA(int a, int b) {
        if (b == 0) {
            return new EEATuple(a, 1, 0);
        }
        EEATuple eea = EEA(b, mod(a, b));
        return new EEATuple(eea.d, eea.y, minus((byte) eea.x, multi((byte)eea.y, div(a, b))));
    }
    byte div(int firstPolynomial, int secondPolynomial) {
        if (secondPolynomial == 1) {
            return (byte) firstPolynomial;
        }

        int degreeOfFirstPolynomial = getDegree(firstPolynomial);
        int degreeOfSecondPolynomial = getDegree(secondPolynomial);

        byte answer = 0;
        while (degreeOfFirstPolynomial >= degreeOfSecondPolynomial && degreeOfFirstPolynomial != 0) {
            int deltaDegree = degreeOfFirstPolynomial - degreeOfSecondPolynomial;
            answer = (byte) (answer | (1 << deltaDegree));

            int polynomialToMinus = secondPolynomial << deltaDegree;

            firstPolynomial = firstPolynomial ^ polynomialToMinus;

            degreeOfFirstPolynomial = getDegree(firstPolynomial);
        }
        return  answer;
    }


    public List<Integer> getAllIrreduciblePolynomials() {
        return new ArrayList<>(allIrreduciblePolynomials);
    }


    public static String polynomialToString(byte polynomial) {
        return String.format("%8s", Integer.toBinaryString(polynomial & 0xFF)).replace(' ', '0');
    }


    public static String polynomialToString(int polynomial) {
        return Integer.toBinaryString(polynomial);
    }
}
