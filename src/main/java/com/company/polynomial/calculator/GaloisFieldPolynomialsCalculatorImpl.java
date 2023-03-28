package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 2^8 Galois field calculator. 283 irreducible polynomial default
 **/
public final class GaloisFieldPolynomialsCalculatorImpl implements GaloisFieldPolynomialsCalculator {
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

    private final Set<Integer> allIrreduciblePolynomials = new HashSet<>();
    public GaloisFieldPolynomialsCalculatorImpl() {
        initIrreduciblePolynomialsSet();
    }

    private void initIrreduciblePolynomialsSet() {
        final int twoDegreeOfGaloisField = 8;
        IntStream
                .range(1 << twoDegreeOfGaloisField, 1 << (twoDegreeOfGaloisField + 1))
                .filter(GaloisFieldPolynomialsCalculatorImpl::polynomialIsIrreducible)
                .forEach(allIrreduciblePolynomials::add);
    }

    @Override
    public byte sum(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial ^ secondPolynomial);
    }

    @Override
    public byte minus(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial ^ secondPolynomial);
    }

    @Override
    public byte multi(byte firstPolynomial, byte secondPolynomial, int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (!polynomialIsIrreducible(irreduciblePolynomial)) {
            throw new WrongIrreduciblePolynomialException();
        }

        int firstPolynomialInt = GaloisFieldPolynomialsCalculator.convertByteToInt(firstPolynomial);
        int secondPolynomialInt = GaloisFieldPolynomialsCalculator.convertByteToInt(secondPolynomial);

        int multipliedGalyaPolynomial = multiPolynomials(firstPolynomialInt, secondPolynomialInt);
        return mod(multipliedGalyaPolynomial, irreduciblePolynomial);
    }
    int multiPolynomials(int firstPolynomial, int secondPolynomial) {
        int result = 0;
        for (int i = 0; i < Byte.SIZE; i++) {
            result ^= firstPolynomial * (secondPolynomial & (1 << i));
        }
        return result;
    }


    @Override
    public byte getReverse(byte polynomial, int irreduciblePolynomial) throws WrongIrreduciblePolynomialException {
        if (!polynomialIsIrreducible(irreduciblePolynomial)) {
            throw new WrongIrreduciblePolynomialException();
        }

        if (polynomial == 0) {
            return 0;
        }

        int polynomialInt = GaloisFieldPolynomialsCalculator.convertByteToInt(polynomial);

        EEATuple eea = EEA(irreduciblePolynomial, polynomialInt);
        return GaloisFieldPolynomialsCalculator.convertIntToByte(eea.y);
    }

    EEATuple EEA(int a, int b) {
        if (b == 0) {
            return new EEATuple(a, 1, 0);
        }
        EEATuple eea = EEA(b, mod(a, b));
        return new EEATuple(eea.d, eea.y, minusForInt(eea.x, multiPolynomials(eea.y, div(a, b))));
    }

    int minusForInt(int firstPolynomial, int secondPolynomial) {
        return firstPolynomial ^ secondPolynomial;
    }

    int plusForInt(int firstPolynomial, int secondPolynomial) {
        return firstPolynomial ^ secondPolynomial;
    }

    int div(int firstPolynomial, int secondPolynomial) {
        if (secondPolynomial == 1) {
            return firstPolynomial;
        }

        int degreeOfFirstPolynomial = getDegree(firstPolynomial);
        int degreeOfSecondPolynomial = getDegree(secondPolynomial);

        int answer = 0;
        while (degreeOfFirstPolynomial >= degreeOfSecondPolynomial) {
            int deltaDegree = degreeOfFirstPolynomial - degreeOfSecondPolynomial;
            answer = answer | (1 << deltaDegree);

            int polynomialToMinus = secondPolynomial << deltaDegree;

            firstPolynomial = firstPolynomial ^ polynomialToMinus;

            degreeOfFirstPolynomial = getDegree(firstPolynomial);
        }
        return  answer;
    }


    @Override
    public List<Integer> getAllIrreduciblePolynomials() {
        return new ArrayList<>(allIrreduciblePolynomials);
    }
}
