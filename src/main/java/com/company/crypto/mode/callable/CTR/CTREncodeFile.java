package com.company.crypto.mode.callable.CTR;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import lombok.Builder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Builder
public class CTREncodeFile implements Callable<Void> {
    private static final int BUFFER_SIZE = 8;

    private final byte[] buffer = new byte[BUFFER_SIZE];

    private final long filePositionToStart;
    private final long byteToEncode;
    private final long indexToStart;
    private final int bufferSize;
    private final int delta;
    private final RandomAccessFile inputFile;
    private final RandomAccessFile outputFile;
    private final SymmetricalBlockEncryptionAlgorithm algorithm;

    @Override
    public Void call() throws Exception {
        inputFile.seek(filePositionToStart);
        outputFile.seek(filePositionToStart);

        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile.getFD()));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile.getFD()));
        ) {
            Arrays.fill(buffer, (byte) 0);

            long i = indexToStart;
            long allReadBytes = 0;
            long read;

            byte[] presentedDigit = new byte[BUFFER_SIZE];
            while ((read = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1 && allReadBytes <= byteToEncode) {
                presentLongAsByteArray(presentedDigit, i);
                byte[] encoded = algorithm.encode(presentedDigit);

                xor(buffer, encoded);
                outputStream.write(buffer);

                Arrays.fill(buffer, (byte) 0);

                allReadBytes += read;
                i += delta;
            }
        }
        return null;
    }
    private void xor(byte[] buffer, byte[] array) {
        for (int i = 0; i < BUFFER_SIZE; i++) {
            buffer[i] = (byte) (buffer[i] ^ array[i]);
        }
    }
    private void presentLongAsByteArray(byte[] buffer, long digit) {
        Arrays.fill(buffer, (byte) 0);
        for (int i = 0; i < buffer.length; i++) {
            buffer[buffer.length - i - 1] = (byte) (digit & 0xFF);
            digit >>= Byte.SIZE;
        }

//        Arrays.fill(buffer, (byte) 0);
//        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
//        byteBuffer.putLong(digit);
//
//        byte[] array = byteBuffer.array();
//        System.arraycopy(array, 0, buffer, 0, array.length);
    }
}
