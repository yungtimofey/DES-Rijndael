package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class CBCCypher implements SymmetricalBlockModeCypher {
    private static final int BUFFER_SIZE = 8;

    private final SymmetricalBlockEncryptionAlgorithm algorithm;
    private final byte[] initialVector;

    private final int threadNumber = Runtime.getRuntime().availableProcessors()-1;
    private final ExecutorService executorService = Executors.newScheduledThreadPool(threadNumber);
    private final byte[] buffer = new byte[BUFFER_SIZE];

    @Override
    public void encode(File inputFile, File outputFile) throws IOException {
        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        ) {
            byte[] toXor = initialVector;

            Arrays.fill(buffer, (byte) 0);
            while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {
                xor(buffer, toXor);
                byte[] encoded = algorithm.encode(buffer);

                outputStream.write(encoded);
                Arrays.fill(buffer, (byte) 0);

                toXor = encoded;
            }
        }
    }
    private void xor(byte[] array1, byte[] array2) {
        BitSet bitSet1 = BitSet.valueOf(array1);
        BitSet bitSet2 = BitSet.valueOf(array2);
        bitSet1.xor(bitSet2);

        byte[] array3 = bitSet1.toByteArray();

        Arrays.fill(array1, (byte) 0);
        System.arraycopy(array3, 0, array1, 0, array3.length);
    }

    @Override
    public void decode(File inputFile, File outputFile) throws IOException {
        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        ) {
            Arrays.fill(buffer, (byte) 0);
            boolean isFirstDecode = true;
            byte[] decoded = null;

            byte[] toXor, previousBuffer = initialVector;
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

    @Override
    public void close() {
        executorService.shutdown();
    }
}
