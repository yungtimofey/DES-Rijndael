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


public class RDCypher extends CTRCypher {
    public RDCypher(SymmetricalBlockEncryptionAlgorithm algorithm, byte[] initialVector) {
        super(algorithm, Runtime.getRuntime().availableProcessors()-1);

        this.delta = initialVector[initialVector.length-1];
        super.startDigit = translateArrayIntoLong(initialVector);
    }
    private long translateArrayIntoLong(byte[] array) {
        long value = 0;
        for (byte b : array) {
            value = (value << Byte.SIZE) + (b & 0xFF);
        }
        return value;
    }
}
