package com.company.polynomial.calculator;

import com.company.polynomial.exception.WrongIrreduciblePolynomialException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class GaliosFieldPolynomialsCalculatorTest {
    static GaloisFieldPolynomialsCalculatorImpl galoisFieldPolynomialsCalculator;
    static int irreduciblePolynomial = 283;

    @BeforeAll
    static void init() {
        galoisFieldPolynomialsCalculator = new GaloisFieldPolynomialsCalculatorImpl();
    }

    @Test
    void checkConverter() {
        byte digit = -103;
        String digitStr = GaloisFieldPolynomialsCalculator.polynomialToString(digit);

        int digitInt = GaloisFieldPolynomialsCalculator.convertByteToInt(digit);
        String intDigitStr = GaloisFieldPolynomialsCalculator.polynomialToString(digitInt);

        assert (digitStr.equals(intDigitStr));
    }


    @Test
    void checkIrreduciblePolynomials() {
        List<Integer> list = galoisFieldPolynomialsCalculator.getAllIrreduciblePolynomials();
        assert (list.size() == 30);
    }

    @Test
    void checkCorrectIrreducibleTest() {
        byte ans = GaloisFieldPolynomialsCalculatorImpl.mod(155, 31);
        assert (ans == 0);
    }

    @Test
    void checkMulti() throws WrongIrreduciblePolynomialException {
        int firstDigit = 87;
        int secondDigit = 131;

        byte firstDigitByte = GaloisFieldPolynomialsCalculator.convertIntToByte(firstDigit);
        byte secondDigitByte = GaloisFieldPolynomialsCalculator.convertIntToByte(secondDigit);

        byte multy = galoisFieldPolynomialsCalculator.multi(firstDigitByte, secondDigitByte, irreduciblePolynomial);
        int converted = GaloisFieldPolynomialsCalculator.convertByteToInt(multy);

        String digit = GaloisFieldPolynomialsCalculator.polynomialToString(converted);
        assert (digit.equals("11000001"));
    }

    @Test
    void checkDiv() {
        int firstDigit = 87;
        int secondDigit = 131;


        int multied = galoisFieldPolynomialsCalculator.multiPolynomials(firstDigit, secondDigit);
        int div = galoisFieldPolynomialsCalculator.div(multied, 283);
        String digit = GaloisFieldPolynomialsCalculator.polynomialToString(div);
        assert (digit.equals("101000"));
    }

    @Test
    void checkReverse() throws WrongIrreduciblePolynomialException {
        for (int i = 1; i < 256; i++) {
            byte digit = GaloisFieldPolynomialsCalculator.convertIntToByte(i);
            byte reversed = galoisFieldPolynomialsCalculator.getReverse(digit, irreduciblePolynomial);

            if (galoisFieldPolynomialsCalculator.multi(digit, reversed, irreduciblePolynomial) != 1) {
                assert(false);
                return;
            }
        }
        assert (true);
    }
}