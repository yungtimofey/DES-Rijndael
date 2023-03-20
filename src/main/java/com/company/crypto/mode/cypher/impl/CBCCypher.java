package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.callable.CBC.CBCDecodeFile;
import com.company.crypto.mode.callable.ECB.ECBDecodeFile;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

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
        for (int i = 0; i < array1.length; i++) {
            array1[i] = (byte) (array1[i] ^ array2[i]);
        }
    }

    @Override
    public void decode(File inputFile, File outputFile) throws IOException {
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / BUFFER_SIZE;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> decodeCallable = CBCDecodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .bufferSize(BUFFER_SIZE)
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
                        .byteToEncode(blockNumber / threadNumber * BUFFER_SIZE)
                        .bufferSize(BUFFER_SIZE)
                        .initialVector(initialVector)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(decodeCallable);

                endOfPreviousBlock += blockNumber/threadNumber * BUFFER_SIZE;
            }

            Callable<Void> decodeCallable = CBCDecodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .bufferSize(BUFFER_SIZE)
                    .initialVector(initialVector)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(decodeCallable);
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
    public void close() {
        executorService.shutdown();
    }
}
