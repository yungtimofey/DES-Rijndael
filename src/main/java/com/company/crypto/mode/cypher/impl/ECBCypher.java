package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.callable.ECB.ECBDecodeFile;
import com.company.crypto.mode.callable.ECB.ECBEncodeFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public final class ECBCypher extends SymmetricalBlockModeCypher {
    public ECBCypher(SymmetricalBlockEncryptionAlgorithm algorithm) {
        super(algorithm, Runtime.getRuntime().availableProcessors()-1);
    }

    @Override
    public void encode(File inputFile, File outputFile) throws IOException {
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / bufferSize;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> encodeCallable = ECBEncodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .bufferSize(bufferSize)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(encodeCallable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber-1; i++) {
                Callable<Void> encodeCallable = ECBEncodeFile.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * bufferSize)
                        .bufferSize(bufferSize)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(encodeCallable);

                endOfPreviousBlock += blockNumber/threadNumber * bufferSize;
            }

            Callable<Void> encodeCallable = ECBEncodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .bufferSize(bufferSize)
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
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / bufferSize;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> decodeCallable = ECBDecodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .bufferSize(bufferSize)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(decodeCallable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber-1; i++) {
                Callable<Void> decodeCallable = ECBDecodeFile.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * bufferSize)
                        .bufferSize(bufferSize)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(decodeCallable);

                endOfPreviousBlock += blockNumber/threadNumber * bufferSize;
            }

            Callable<Void> decodeCallable = ECBDecodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .bufferSize(bufferSize)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(decodeCallable);
        }

        callTasksAndWait(callableList);
    }


}
