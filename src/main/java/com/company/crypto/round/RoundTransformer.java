package com.company.crypto.round;


import java.util.BitSet;

/**
 * Makes one round of symmetric algorithm
 **/
public abstract class RoundTransformer {
    public abstract byte[] encode(byte[] inputBlock, byte[] roundKey, boolean isLastRound);
    public abstract byte[] decode(byte[] inputBlock, byte[] roundKey, boolean isLastRound);

    protected abstract BitSet sPermutation(BitSet expandedHalf, int currentGroupSize, int newGroupSize);
}