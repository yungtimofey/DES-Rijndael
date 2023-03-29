package com.company.crypto.mode.callable.RDPlusH;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.padding.PKCS7;
import lombok.Builder;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Builder
public class RDPlusHEncode implements Callable<Void> {
    private byte[] buffer;

    private final long filePositionToStart;
    private final long byteToEncode;
    private final long indexToStart;
    private final int bufferSize;
    private final int delta;
    private final byte[] hash;
    private final RandomAccessFile inputFile;
    private final RandomAccessFile outputFile;
    private final SymmetricalBlockEncryptionAlgorithm algorithm;

    @Override
    public Void call() throws Exception {
        buffer = new byte[bufferSize];

        inputFile.seek(filePositionToStart);
        outputFile.seek(filePositionToStart);

        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile.getFD()));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile.getFD()));
        ) {
            long i = indexToStart;
            long allReadBytes = 0;
            long read;

            byte[] previousOpenBlock = new byte[bufferSize];
            getPreviousOpenBlock(previousOpenBlock, inputStream);

            byte[] presentedDigit = new byte[bufferSize];
            while ((read = inputStream.read(buffer, 0, bufferSize)) != -1 && allReadBytes <= byteToEncode) {
                if (read < bufferSize) {
                    PKCS7.doPadding(buffer, (int) (bufferSize - read));
                }

                presentLongAsByteArray(presentedDigit, i);
                byte[] encoded = algorithm.encode(presentedDigit);

                xor(buffer, encoded);
                xor(buffer, previousOpenBlock);
                outputStream.write(buffer);

                allReadBytes += read;
                i += delta;
            }
        }
        return null;
    }

    private void getPreviousOpenBlock(byte[] previousOpenBlock, InputStream inputStream) throws IOException {
        if (inputFile.getFilePointer() == 0) {
            System.arraycopy(hash, 0, previousOpenBlock, 0, bufferSize);
        }

        inputFile.seek(inputFile.getFilePointer() - bufferSize);
        if (inputStream.read(buffer, 0, bufferSize) != bufferSize) {
            throw new IllegalArgumentException("Wrong file position!");
        }
        System.arraycopy(hash, 0, buffer, 0, bufferSize);
    }

    private void xor(byte[] buffer, byte[] array) {
        for (int i = 0; i < bufferSize; i++) {
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
