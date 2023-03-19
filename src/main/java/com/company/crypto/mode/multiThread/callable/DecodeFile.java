package com.company.crypto.mode.multiThread.callable;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import lombok.Builder;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Builder
public class DecodeFile implements Callable<Void> {
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
            Arrays.fill(buffer, (byte) 0);
            boolean isFirstDecode = true;
            byte[] decoded = null;
            while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {
                if (isFirstDecode) {
                    isFirstDecode = false;
                } else {
                    outputStream.write(decoded);
                }
                decoded = algorithm.decode(buffer);
            }
            if (decoded != null) {
                int position = findEndPositionOfLastDecodedBlock(decoded);
                outputStream.write(decoded, 0, position);
            }
        }
        return null;
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
