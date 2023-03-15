package com.company.crypto.round;


/**
 * Makes one round of symmetric algorithm
 **/
public interface RoundTransformer {
    byte[] doRound(byte[] inputBlock64Bit, byte[] roundKey56Bit, boolean isLastRound);
}