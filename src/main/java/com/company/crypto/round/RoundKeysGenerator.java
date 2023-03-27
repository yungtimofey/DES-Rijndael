package com.company.crypto.round;


/**
 * Generate all round keys
 **/
public interface RoundKeysGenerator {
    byte[][] generate(byte[] cipherKey);
}
