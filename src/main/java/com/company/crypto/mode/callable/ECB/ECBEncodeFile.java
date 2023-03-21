package com.company.crypto.mode.callable.ECB;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.padding.PKCS7;
import lombok.Builder;

import java.io.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.Callable;

@Builder
public class ECBEncodeFile implements Callable<Void> {
    private static final int BUFFER_SIZE = 8;

    private final byte[] buffer = new byte[BUFFER_SIZE];

    private final long filePositionToStart;
    private final long byteToEncode;
    private final int bufferSize;
    private final RandomAccessFile inputFile;
    private final RandomAccessFile outputFile;
    private final SymmetricalBlockEncryptionAlgorithm algorithm;

    @Override
    public Void call() throws IOException {
        inputFile.seek(filePositionToStart);
        outputFile.seek(filePositionToStart);

        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile.getFD()));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile.getFD()));
        ) {
            long allReadBytes = 0;
            long read;
            while ((read = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1 && allReadBytes <= byteToEncode) {
                if (read < BUFFER_SIZE) {
                    PKCS7.doPadding(buffer, (int) (BUFFER_SIZE - read));
                }

                byte[] encoded = algorithm.encode(buffer);
                outputStream.write(encoded);

                allReadBytes += read;
            }
        }
        return null;
    }



}
