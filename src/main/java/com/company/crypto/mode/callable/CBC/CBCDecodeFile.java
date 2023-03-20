package com.company.crypto.mode.callable.CBC;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import lombok.Builder;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Builder
public class CBCDecodeFile implements Callable<Void> {
    private static final int BUFFER_SIZE = 8;

    private final byte[] buffer = new byte[BUFFER_SIZE];

    private final long filePositionToStart;
    private final long byteToEncode;
    private final int bufferSize;
    private final RandomAccessFile inputFile;
    private final RandomAccessFile outputFile;
    private final SymmetricalBlockEncryptionAlgorithm algorithm;
    private final byte[] initialVector;

    @Override
    public Void call() throws IOException {
        inputFile.seek(filePositionToStart);
        outputFile.seek(filePositionToStart);

        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile.getFD()));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile.getFD()));
        ) {
            Arrays.fill(buffer, (byte) 0);
            boolean isFirstDecode = true;
            byte[] decoded = null;

            byte[] toXor;
            byte[] previousBuffer = new byte[BUFFER_SIZE];
            System.arraycopy(getInitialVector(inputStream), 0, previousBuffer, 0, BUFFER_SIZE);

            Arrays.fill(buffer, (byte) 0);
            while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {
                if (isFirstDecode) {
                    isFirstDecode = false;
                } else {
                    outputStream.write(decoded);
                }

                decoded = algorithm.decode(buffer);

                toXor = previousBuffer;
                xor(decoded, toXor);
                System.arraycopy(buffer, 0, previousBuffer, 0, decoded.length);
            }
            if (decoded != null) {
                int position = findEndPositionOfLastDecodedBlock(decoded);
                outputStream.write(decoded, 0, position);
            }
        }
        return null;
    }

    private byte[] getInitialVector(InputStream inputStream) throws IOException {
        if (inputFile.getFilePointer() == 0) {
            return initialVector;
        }

        inputFile.seek(inputFile.getFilePointer() - BUFFER_SIZE);
        if (inputStream.read(buffer, 0, BUFFER_SIZE) != BUFFER_SIZE) {
            throw new IllegalArgumentException("Wrong file position!");
        }
        return buffer;
    }

    private void xor(byte[] array1, byte[] array2) {
        for (int i = 0; i < array1.length; i++) {
            array1[i] = (byte) (array1[i] ^ array2[i]);
        }
    }
    private int findEndPositionOfLastDecodedBlock(byte[] decoded) {
        int position;
        for (position = 0; position < decoded.length; position++) {
            if (decoded[position] == 0) {
                break;
            }
        }
        return position;
    }
}
