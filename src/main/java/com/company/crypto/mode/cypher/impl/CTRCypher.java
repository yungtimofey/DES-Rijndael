package com.company.crypto.mode.cypher.impl;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;
import com.company.crypto.mode.callable.CTR.CTRDecodeFile;
import com.company.crypto.mode.callable.CTR.CTREncodeFile;
import com.company.crypto.mode.cypher.SymmetricalBlockModeCypher;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CTRCypher extends SymmetricalBlockModeCypher {
    protected long startDigit;
    protected int delta;

    public CTRCypher(SymmetricalBlockEncryptionAlgorithm algorithm, int startDigit) {
        super(algorithm, Runtime.getRuntime().availableProcessors() - 1);

        this.startDigit = startDigit;
        this.delta = 1;
    }

    @Override
    public void encode(File inputFile, File outputFile) throws IOException {
        long fileLengthInByte = inputFile.length();
        long blockNumber = fileLengthInByte / bufferSize;

        List<Callable<Void>> callableList = new ArrayList<>();
        if (blockNumber < threadNumber || threadNumber < 2) {
            Callable<Void> encodeCallable = CTREncodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .indexToStart(this.startDigit)
                    .delta(delta)
                    .bufferSize(bufferSize)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(encodeCallable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber - 1; i++) {
                Callable<Void> encodeCallable = CTREncodeFile.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * bufferSize)
                        .indexToStart(endOfPreviousBlock / bufferSize * delta + startDigit)
                        .delta(delta)
                        .bufferSize(bufferSize)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(encodeCallable);

                endOfPreviousBlock += blockNumber / threadNumber * bufferSize;
            }

            Callable<Void> encodeCallable = CTREncodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .indexToStart(endOfPreviousBlock / bufferSize * delta + startDigit)
                    .delta(delta)
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
            Callable<Void> decodeCallable = CTRDecodeFile.builder()
                    .filePositionToStart(0)
                    .byteToEncode(fileLengthInByte)
                    .startDigit(this.startDigit)
                    .delta(delta)
                    .bufferSize(bufferSize)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(decodeCallable);
        } else {
            long endOfPreviousBlock = 0;
            for (int i = 0; i < threadNumber - 1; i++) {
                Callable<Void> decodeCallable = CTRDecodeFile.builder()
                        .filePositionToStart(endOfPreviousBlock)
                        .byteToEncode(blockNumber / threadNumber * bufferSize)
                        .bufferSize(bufferSize)
                        .delta(delta)
                        .startDigit(endOfPreviousBlock / bufferSize * delta + startDigit)
                        .algorithm(algorithm)
                        .inputFile(new RandomAccessFile(inputFile, "r"))
                        .outputFile(new RandomAccessFile(outputFile, "rw"))
                        .build();
                callableList.add(decodeCallable);

                endOfPreviousBlock += blockNumber / threadNumber * bufferSize;
            }

            Callable<Void> decodeCallable = CTRDecodeFile.builder()
                    .filePositionToStart(endOfPreviousBlock)
                    .byteToEncode(fileLengthInByte - endOfPreviousBlock)
                    .bufferSize(bufferSize)
                    .delta(delta)
                    .startDigit(endOfPreviousBlock / bufferSize * delta + startDigit)
                    .algorithm(algorithm)
                    .inputFile(new RandomAccessFile(inputFile, "r"))
                    .outputFile(new RandomAccessFile(outputFile, "rw"))
                    .build();
            callableList.add(decodeCallable);
        }
        callTasksAndWait(callableList);
    }
}
