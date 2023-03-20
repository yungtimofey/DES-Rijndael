package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;

import java.io.*;
import java.util.Arrays;

public class CFBCypher extends SymmetricalBlockModeCypher {
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private byte[] initialVector;

    protected CFBCypher(SymmetricalBlockEncryptionAlgorithm algorithm, byte[] initialVector) {
        super(algorithm, 0);

        this.initialVector = initialVector;
    }

    @Override
    public void encode(File inputFile, File outputFile) throws IOException {
        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        ) {
            byte[] toEncode = new byte[BUFFER_SIZE];
            System.arraycopy(initialVector, 0, toEncode, 0, initialVector.length);

            Arrays.fill(buffer, (byte) 0);
            while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {
                byte[] encoded = algorithm.encode(toEncode);

                xor(buffer, encoded);
                System.arraycopy(buffer, 0, toEncode, 0, encoded.length);

                outputStream.write(buffer);
                Arrays.fill(buffer, (byte) 0);
            }
        }
    }

    @Override
    public void decode(File inputFile, File outputFile) throws IOException {
        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        ) {
            Arrays.fill(buffer, (byte) 0);

            byte[] xored = new byte[BUFFER_SIZE];
            byte[] toEncode = new byte[BUFFER_SIZE];
            System.arraycopy(initialVector, 0, toEncode, 0, initialVector.length);

            boolean isFirstDecode = true;
            while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {
                if (isFirstDecode) {
                    isFirstDecode = false;
                } else {
                    outputStream.write(xored);
                }

                byte[] encoded = algorithm.encode(toEncode);

                xor(buffer, encoded);
                System.arraycopy(buffer, 0, toEncode, 0, BUFFER_SIZE);

                System.arraycopy(buffer, 0, xored, 0, BUFFER_SIZE);
            }
            if (!isFirstDecode) {
                int position = findEndPositionOfLastDecodedBlock(xored);
                outputStream.write(xored, 0, position);
            }
        }
    }
}
