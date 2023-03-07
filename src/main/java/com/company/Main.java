package com.company;

import com.company.crypto.Cypher;
import com.company.crypto.algorithm.impl.DES;
import com.company.crypto.mode.SymmetricalBlockMode;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Cypher cypher = Cypher.build(
                new byte[0],
                SymmetricalBlockMode.ECB,
                DES.class
        );

        cypher.encode(new File("in.txt"), new File("out.txt"));
    }
}
