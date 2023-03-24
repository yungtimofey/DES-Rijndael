package com.company.polynomial.calculator;

public class GalyaFieldPolynomialsCalculator {
    public static int n = 8;

    public static byte sum(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial ^ secondPolynomial);
    }

    public static byte multi(byte firstPolynomial, byte secondPolynomial) {
        return (byte) (firstPolynomial * secondPolynomial % n);
    }



}
