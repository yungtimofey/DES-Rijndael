package com.company;


import java.io.*;
import java.nio.file.Path;
import java.util.BitSet;

/*
is
retur
 */

public class Main {
    public static void main(String[] args) throws IOException {
        byte a = 123;
        byte b = 122;

        int c =  (a ^ b);
        System.out.println(c);
    }

    private static void print(BitSet bitSet) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bitSet.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                s.append(' ');
            }
            s.append(bitSet.get(i) ? 1 : 0);
        }
        System.out.println(s);
    }
}
