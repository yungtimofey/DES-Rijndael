package com.company.crypto.mode.callable.CBC;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.padding.PKCS7;
import lombok.Builder;

import java.io.*;
import java.util.concurrent.Callable;

@Builder
public class CBCDecodeFile implements Callable<Void> {
    private final long filePositionToStart;
    private final long byteToEncode;
    private final int bufferSize;
    private final RandomAccessFile inputFile;
    private final RandomAccessFile outputFile;
    private final SymmetricalBlockEncryptionAlgorithm algorithm;
    private final byte[] initialVector;

    private byte[] buffer;

    @Override
    public Void call() throws IOException {
        buffer = new byte[bufferSize];

        inputFile.seek(filePositionToStart);
        outputFile.seek(filePositionToStart);

        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile.getFD()));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile.getFD()));
        ) {
            boolean isFirstDecode = true;
            byte[] decoded = null;

            byte[] toXor;
            byte[] previousBuffer = new byte[bufferSize];
            System.arraycopy(getInitialVector(inputStream), 0, previousBuffer, 0, bufferSize);

            long read;
            long allReadBytes = 0;
            while ((read = inputStream.read(buffer, 0, bufferSize)) != -1 && allReadBytes <= byteToEncode) {
                if (isFirstDecode) {
                    isFirstDecode = false;
                } else {
                    outputStream.write(decoded);
                }

                decoded = algorithm.decode(buffer);

                toXor = previousBuffer;
                xor(decoded, toXor);
                System.arraycopy(buffer, 0, previousBuffer, 0, decoded.length);

                allReadBytes += read;
            }
            if (!isFirstDecode) {
                int position = PKCS7.getPositionOfFinishByte(decoded);
                outputStream.write(decoded, 0, position);
            }
        }
        return null;
    }

    private byte[] getInitialVector(InputStream inputStream) throws IOException {
        if (inputFile.getFilePointer() == 0) {
            return initialVector;
        }

        inputFile.seek(inputFile.getFilePointer() - bufferSize);
        if (inputStream.read(buffer, 0, bufferSize) != bufferSize) {
            throw new IllegalArgumentException("Wrong file position!");
        }
        return buffer;
    }

    private void xor(byte[] array1, byte[] array2) {
        for (int i = 0; i < array1.length; i++) {
            array1[i] = (byte) (array1[i] ^ array2[i]);
        }
    }
}
