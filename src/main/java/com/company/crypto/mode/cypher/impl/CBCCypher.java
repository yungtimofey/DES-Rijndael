package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.callable.CBC.CBCDecodeFile;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.padding.PKCS7;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;


public class CBCCypher extends SymmetricalBlockModeCypher {
    private final byte[] initialVector;

    public CBCCypher(SymmetricalBlockEncryptionAlgorithm algorithm, byte[] initialVector) {
        super(algorithm, Runtime.getRuntime().availableProcessors()-1);
        this.initialVector = initialVector;
    }

    @Override
    public void encode(File inputFile, File outputFile) throws IOException {
        try (
                InputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        ) {
            byte[] toXor = initialVector;
            long read;

            while ((read = inputStream.read(buffer, 0, bufferSize)) != -1) {
                if (read < bufferSize) {
                    PKCS7.doPadding(buffer, (int) (bufferSize - read));
                }

                xor(buffer, toXor);
                byte[] encoded = algorithm.encode(buffer);

                outputStream.write(encoded);

                toXor = encoded;
            }
        }
    }

    @Override
    public void decode(File inputFile, File outputFile) throws IOException {
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / bufferSize;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> decodeCallable = CBCDecodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .bufferSize(bufferSize)
                    .initialVector(initialVector)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(decodeCallable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber-1; i++) {
                Callable<Void> decodeCallable = CBCDecodeFile.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * bufferSize)
                        .bufferSize(bufferSize)
                        .initialVector(initialVector)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(decodeCallable);

                endOfPreviousBlock += blockNumber/threadNumber * bufferSize;
            }

            Callable<Void> decodeCallable = CBCDecodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .bufferSize(bufferSize)
                    .initialVector(initialVector)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(decodeCallable);
        }
        callTasksAndWait(callableList);
    }
}
