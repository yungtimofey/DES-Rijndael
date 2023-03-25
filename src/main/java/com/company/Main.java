package com.company;


public class Main {
   public static void main(String[] args) {
       byte a = -113;
       System.out.println(polynomialToString(a));

       long b = a;
       System.out.println(Long.toBinaryString(b));

       b = 0;
       for (int i = 0; i < Byte.SIZE; i++) {
           b |= a & (1 << i);
       }
       System.out.println(Long.toBinaryString(b));
   }

    public static String polynomialToString(byte polynomial) {
        return String.format("%8s", Integer.toBinaryString(polynomial & 0xFF)).replace(' ', '0');
    }

}
