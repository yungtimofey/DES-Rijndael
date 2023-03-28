package com.company.crypto.round;


import java.util.BitSet;

/**
 * Makes one round of symmetric algorithm
 **/
public interface  RoundTransformer {
    byte[] encode(byte[] inputBlock, byte[] roundKey, boolean isLastRound);
    byte[] decode(byte[] inputBlock, byte[] roundKey, boolean isLastRound);
}