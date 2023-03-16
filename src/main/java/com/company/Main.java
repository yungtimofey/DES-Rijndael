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
        byte[] buffer = new byte[]{'i', 's','\n', 'r', 'e', 't', 'u'};
        print(BitSet.valueOf(buffer));

        // 1001 0110 1100 1110 1011 0000 0101 0000 0100 1110 1010 0110 0010 1110 1010 111
        // 1001 0110 1100 1110 0101 0000 0100 1110 1010 0110 0010 1110 1010 111

        buffer = new byte[] {'r'};
        print(BitSet.valueOf(buffer));

        buffer = new byte[] {0, 0, 1};
        print(BitSet.valueOf(buffer));

        OutputStream outputStream = new FileOutputStream(new File("in.txt"));
        outputStream.write(new byte[]{0});

        outputStream.write(new byte[]{'a'});
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
