package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.padding.PKCS7;

import java.io.*;
import java.util.Arrays;


public class OFBCypher extends SymmetricalBlockModeCypher {
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private final byte[] initialVector;

    public OFBCypher(SymmetricalBlockEncryptionAlgorithm algorithm, byte[] initialVector) {
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

            long read;
            Arrays.fill(buffer, (byte) 0);
            while ((read = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                if (read < BUFFER_SIZE) {
                    PKCS7.doPadding(buffer, (int) (BUFFER_SIZE - read));
                }

                byte[] encoded = algorithm.encode(toEncode);
                toEncode = encoded;

                xor(buffer, encoded);

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
                toEncode = encoded;

                xor(buffer, encoded);

                System.arraycopy(buffer, 0, xored, 0, BUFFER_SIZE);
            }
            if (!isFirstDecode) {
                int position = PKCS7.getPositionOfFinishByte(xored);
                outputStream.write(xored, 0, position);
            }
        }
    }
}