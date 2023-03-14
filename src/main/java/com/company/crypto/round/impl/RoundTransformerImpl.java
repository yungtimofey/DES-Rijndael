package com.company.crypto.round.impl;

import com.company.crypto.round.RoundTransformer;

public class RoundTransformerImpl implements RoundTransformer {
    @Override
    public byte[] encode(byte[] inputBlock, byte[] roundKey) {
        return new byte[0];
    }

    @Override
    public byte[] decode(byte[] inputBlock, byte[] roundKey) {
        return new byte[0];
    }
}
