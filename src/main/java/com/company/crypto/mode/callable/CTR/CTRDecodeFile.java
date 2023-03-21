package com.company.crypto.mode.callable.CTR;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.padding.PKCS7;
import lombok.Builder;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Builder
public class CTRDecodeFile implements Callable<Void> {
    private static final int BUFFER_SIZE = 8;

    private final byte[] buffer = new byte[BUFFER_SIZE];

    private final long filePositionToStart;
    private final long byteToEncode;
    private final long startDigit;
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

            long i = startDigit;
            boolean isFirstDecode = true;
            byte[] encoded = null;

            byte[] presentedDigit = new byte[BUFFER_SIZE];
            long allReadBytes = 0, read;
            while ((read = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1 && allReadBytes <= byteToEncode) {
                if (isFirstDecode) {
                    isFirstDecode = false;
                } else {
                    outputStream.write(encoded);
                    i += delta;
                }

                presentLongAsByteArray(presentedDigit, i);
                encoded = algorithm.encode(presentedDigit);
                xor(encoded, buffer);

                allReadBytes += read;
            }
            if (!isFirstDecode) {
                int position = PKCS7.doDepadding(encoded);
                outputStream.write(encoded, 0, position);
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
    }
}
