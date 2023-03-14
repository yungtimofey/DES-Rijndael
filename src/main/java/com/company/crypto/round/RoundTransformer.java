package com.company.crypto.round;


/**
 * Makes one round of symmetric algorithm
 **/
public interface RoundTransformer {
    byte[] encode(byte[] inputBlock64Bit, byte[] roundKey56Bit);
    byte[] decode(byte[] outputBlock64Bit, byte[] roundKey56Bit);
}