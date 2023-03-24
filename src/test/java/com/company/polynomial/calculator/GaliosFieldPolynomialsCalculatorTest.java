package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

class GaliosFieldPolynomialsCalculatorTest {
    static GaloisFieldPolynomialsCalculator galoisFieldPolynomialsCalculator;
    @BeforeAll
    static void init() throws WrongIrreduciblePolynomialException {
        galoisFieldPolynomialsCalculator = GaloisFieldPolynomialsCalculator.getInstance(283);
    }

    @Test
    void checkClassExample() {
        byte polynomial = galoisFieldPolynomialsCalculator.multi((byte) 87, (byte) 131);
        String polynomialStr = GaloisFieldPolynomialsCalculator.polynomialToString(polynomial);

        assert (polynomialStr.equals("11000001"));
    }


}