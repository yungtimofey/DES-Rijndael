package com.company.crypto.mode.cypher;

import com.company.crypto.algorithm.SymmetricalBlockEncryptionAlgorithm;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Its class is used by Cypher. Makes encode and decode
 */
public abstract class SymmetricalBlockModeCypher implements Closeable {
    protected static final int BUFFER_SIZE = 8;

    protected final int threadNumber;
    protected final ExecutorService executorService;
    protected final SymmetricalBlockEncryptionAlgorithm algorithm;

    protected SymmetricalBlockModeCypher(SymmetricalBlockEncryptionAlgorithm algorithm, int threadNumber) {
        this.algorithm = algorithm;
        this.threadNumber = threadNumber;

        this.executorService = (threadNumber > 0) ? Executors.newScheduledThreadPool(threadNumber) : null;
    }

    public abstract void encode(File inputFile, File outputFile) throws IOException;
    public abstract void decode(File inputFile, File outputFile) throws IOException;

    protected void callTasksAndWait(List<Callable<Void>> callableList) {
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

    protected void xor(byte[] array1, byte[] array2) {
        for (int i = 0; i < array1.length; i++) {
            array1[i] = (byte) (array1[i] ^ array2[i]);
        }
    }

    protected int findEndPositionOfLastDecodedBlock(byte[] decoded) {
        int position;
        for (position = 0; position < decoded.length; position++) {
            if (decoded[position] == 0) {
                break;
            }
        }
        return position;
    }

    protected void presentLongAsByteArray(byte[] buffer, long digit) {
        Arrays.fill(buffer, (byte) 0);
        for (int i = 0; i < buffer.length; i++) {
            buffer[buffer.length - i - 1] = (byte) (digit & 0xFF);
            digit >>= Byte.SIZE;
        }
    }

    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
