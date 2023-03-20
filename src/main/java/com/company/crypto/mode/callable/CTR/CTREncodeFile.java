package com.company.crypto.mode.callable.CTR;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import lombok.Builder;

import java.io.*;
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

            long allReadBytes = 0, read;
            long i = indexToStart;
            while ((read = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1 && allReadBytes < byteToEncode) {
                makeSum(buffer, i);
                byte[] encoded = algorithm.encode(buffer);
                outputStream.write(encoded);

                Arrays.fill(buffer, (byte) 0);

                allReadBytes += read;
                i++;
            }
        }

        return null;
    }

    private void makeSum(byte[] buffer, long i) {

    }
}
