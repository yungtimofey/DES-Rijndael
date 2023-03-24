package com.company;


import java.nio.ByteBuffer;
import java.util.BitSet;

public class Main {
    static int min_poly = 283;

   public static void main(String[] args) {
       byte ans = multi((byte) 87, (byte) 131);
       out(ans);

       ans = multi((byte) 1, (byte) 0);
       out(ans);
   }

    static void out(byte a) {
        String s2 = String.format("%8s", Integer.toBinaryString(a & 0xFF)).replace(' ', '0');
        System.out.println(s2); // 000000
    }

    static byte multi(byte a, byte b) {
        int multiResult = 0;
        for (int i = 0; i < 8; i++) {
            multiResult ^= a * (b&(1 << i));
        }
        return mod(multiResult, min_poly);
    }

    static byte mod(int a, int b) {
       int largestDegree = getLargestDegree(a);
       final int largestPolynomialDegree = 8;

       while (largestDegree >= largestPolynomialDegree) {
           int deltaDegree = largestDegree - largestPolynomialDegree;
           int toMinus = b << deltaDegree;

           //System.out.println(Integer.toBinaryString(toMinus));
           //System.out.println(Integer.toBinaryString(a));
           a = a ^ toMinus;
          // System.out.println(Integer.toBinaryString(a));

           largestDegree = getLargestDegree(a);
       }
       return (byte) a;
   }

    static int getLargestDegree(int a) {
       if (a == 0) return 0;

       int degree = 0;
       while (a > 0) {
           degree++;
           a = a >> 1;
       }
       return degree-1;
    }
}
