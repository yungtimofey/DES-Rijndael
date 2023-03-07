package com.company.crypto.round;


/**
 * Makes one round of symmetric algorithm
 **/
public interface RoundTransformer {
    byte[] encode(byte[] inputBlock, byte[] roundKey);
    byte[] decode(byte[] inputBlock, byte[] roundKey);
}