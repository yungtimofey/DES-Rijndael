package com.company.crypto.mode.cypher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Its class is used by Cypher. Makes encode and decode
 */
public abstract class SymmetricalBlockModeCypher implements Closeable {
    protected ExecutorService executorService;

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

    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
