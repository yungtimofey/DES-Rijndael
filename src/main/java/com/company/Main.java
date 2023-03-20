package com.company;


import java.io.*;
import java.nio.ByteBuffer;
import java.util.BitSet;


public class Main {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(1);

        byte[] array = buffer.array();
        System.out.println();
    }
    private void makeSum(byte[] buffer, long i) {
        long digit = presentArrayAsLong(buffer);
        digit += i;
        presentLongAsByteArray(buffer, digit);
    }
    private long presentArrayAsLong(byte[] buffer) {
        long digit = 0;
        for (byte b : buffer) {
            digit = (digit << 8) + (b & 0xFF);
        }
        return digit;
    }
    private void presentLongAsByteArray(byte[] buffer, long digit) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[buffer.length - i - 1] = (byte) (digit & 0xFF);
            digit >>= 8;
        }
    }
}
