package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class GaliosFieldPolynomialsCalculatorTest {
    static GaloisFieldPolynomialsCalculator galoisFieldPolynomialsCalculator;
    @BeforeAll
    static void init() throws WrongIrreduciblePolynomialException {
        galoisFieldPolynomialsCalculator = GaloisFieldPolynomialsCalculator.getInstance(283);
    }

    @Test
    void checkMultiClassExample() {
        byte polynomial = galoisFieldPolynomialsCalculator.multi((byte) 87, (byte) 131);
        String polynomialStr = GaloisFieldPolynomialsCalculator.polynomialToString(polynomial);

        assert (polynomialStr.equals("11000001"));
    }

    @Test
    void checkIrreduciblePolynomials() {
        List<Integer> list = galoisFieldPolynomialsCalculator.getAllIrreduciblePolynomials();
        assert (list.size() == 30);
    }

    @Test
    void checkCorrectIrreducibleTest() {
        byte ans = GaloisFieldPolynomialsCalculator.mod(155, 31);
        assert (ans == 0);
    }

    @Test
    void checkReverse() {
        GaloisFieldPolynomialsCalculator.EEATuple eea = galoisFieldPolynomialsCalculator.EEA(150, 283);

        byte firstMulti = galoisFieldPolynomialsCalculator.multi((byte) 150, (byte) eea.x);
        byte secondMulti = galoisFieldPolynomialsCalculator.multi((byte) 283, (byte) eea.y);

        assert (galoisFieldPolynomialsCalculator.sum(firstMulti, secondMulti) == 1);

    }

    @Test
    void checkDiv() {
        int polynomial = galoisFieldPolynomialsCalculator.multiPolynomials((byte) 87, (byte) 131);
        //System.out.println(GaloisFieldPolynomialsCalculator.polynomialToString(polynomial));

        byte ans = galoisFieldPolynomialsCalculator.div(polynomial, 283);
        String polynomialStr = GaloisFieldPolynomialsCalculator.polynomialToString(ans);
        //System.out.println(polynomialStr);

        assert (polynomialStr.equals("00101000"));
    }
}