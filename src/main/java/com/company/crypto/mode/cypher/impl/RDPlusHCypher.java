package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.callable.RDPlusH.RDPlusHEncode;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class RDPlusHCypher extends SymmetricalBlockModeCypher {
    private final long startDigit;
    private final int delta;
    private final byte[] hash;
    private final byte[] buffer = new byte[BUFFER_SIZE];

    public RDPlusHCypher(SymmetricalBlockEncryptionAlgorithm algorithm, byte[] initialVector, byte[] hash) {
        super(algorithm, Runtime.getRuntime().availableProcessors()-1);

        this.delta = initialVector[initialVector.length-1];
        this.startDigit = translateArrayIntoLong(initialVector);
        this.hash = hash;
    }
    private long translateArrayIntoLong(byte[] array) {
        long value = 0;
        for (byte b : array) {
            value = (value << Byte.SIZE) + (b & 0xFF);
        }
        return value;
    }

    @Override
    public void encode(File inputFile, File outputFile) throws IOException {
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / BUFFER_SIZE;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> encodeCallable = RDPlusHEncode.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .indexToStart(this.startDigit)
                    .delta(delta)
                    .hash(hash)
                    .bufferSize(BUFFER_SIZE)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(encodeCallable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber - 1; i++) {
                Callable<Void> encodeCallable = RDPlusHEncode.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * BUFFER_SIZE)
                        .indexToStart(endOfPreviousBlock / BUFFER_SIZE * delta + startDigit)
                        .delta(delta)
                        .hash(hash)
                        .bufferSize(BUFFER_SIZE)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(encodeCallable);

                endOfPreviousBlock += blockNumber / threadNumber * BUFFER_SIZE;
            }

            Callable<Void> encodeCallable = RDPlusHEncode.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .indexToStart(endOfPreviousBlock / BUFFER_SIZE * delta + startDigit)
                    .hash(hash)
                    .delta(delta)
                    .bufferSize(BUFFER_SIZE)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(encodeCallable);
        }

        callTasksAndWait(callableList);
    }

    @Override
    public void decode(File inputFile, File outputFile) throws IOException {
        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        ) {
            Arrays.fill(buffer, (byte) 0);

            long i = startDigit;
            boolean isFirstDecode = true;
            byte[] encoded = null;

            byte[] previousOpenBlock = new byte[BUFFER_SIZE];
            System.arraycopy(hash, 0, previousOpenBlock, 0, BUFFER_SIZE);

            byte[] presentedDigit = new byte[BUFFER_SIZE];
            while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {
                if (isFirstDecode) {
                    isFirstDecode = false;
                } else {
                    outputStream.write(encoded);
                    System.arraycopy(encoded, 0, previousOpenBlock, 0, BUFFER_SIZE);

                    i += delta;
                }

                presentLongAsByteArray(presentedDigit, i);
                encoded = algorithm.encode(presentedDigit);
                xor(encoded, buffer);
                xor(encoded, previousOpenBlock);
            }
            if (encoded != null) {
                int position = findEndPositionOfLastDecodedBlock(encoded);
                outputStream.write(encoded, 0, position);
            }
        }
    }
}
