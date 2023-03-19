package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import com.company.crypto.mode.multiThread.callable.DecodeFile;
import com.company.crypto.mode.multiThread.callable.EncodeFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class ECBCypher implements SymmetricalBlockModeCypher {
    private static final int BUFFER_SIZE = 8;

    private final SymmetricalBlockEncryptionAlgorithm algorithm;

    private final int threadNumber;
    private final ExecutorService executorService;

    public ECBCypher(SymmetricalBlockEncryptionAlgorithm algorithm) {
        this.algorithm = algorithm;

        this.threadNumber = Runtime.getRuntime().availableProcessors()-1;
        this.executorService = Executors.newScheduledThreadPool(threadNumber);
    }

    @Override
    public void encode(File inputFile, File outputFile) throws IOException {
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / BUFFER_SIZE;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> callable = EncodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .bufferSize(BUFFER_SIZE)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(callable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber-1; i++) {
                Callable<Void> callable = EncodeFile.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * BUFFER_SIZE)
                        .bufferSize(BUFFER_SIZE)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(callable);

                endOfPreviousBlock += blockNumber/threadNumber * BUFFER_SIZE;
            }

            Callable<Void> callable = EncodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .bufferSize(BUFFER_SIZE)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(callable);
        }

        callTasksAndWait(callableList);

    }
    private void callTasksAndWait(List<Callable<Void>> callableList) {
        List<Future<Void>> futureList = new ArrayList<>();
        callableList.forEach(encodeCallable -> futureList.add(executorService.submit(encodeCallable)));
        futureList.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void decode(File inputFile, File outputFile) throws IOException {
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / BUFFER_SIZE;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> callable = DecodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .bufferSize(BUFFER_SIZE)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(callable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber-1; i++) {
                Callable<Void> callable = DecodeFile.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * BUFFER_SIZE)
                        .bufferSize(BUFFER_SIZE)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(callable);

                endOfPreviousBlock += blockNumber/threadNumber * BUFFER_SIZE;
            }

            Callable<Void> callable = DecodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .bufferSize(BUFFER_SIZE)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(callable);
        }

        callTasksAndWait(callableList);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
