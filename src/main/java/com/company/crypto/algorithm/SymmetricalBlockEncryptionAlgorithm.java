package com.company.crypto.algorithm;


/**
 * Encode/decode 64 bit. Gets key generator and round transformer
 **/
public interface SymmetricalBlockEncryptionAlgorithm {
    byte[] decode(byte[] outputBlock64Bit);
    byte[] encode(byte[] inputBlock64Bit);
}
