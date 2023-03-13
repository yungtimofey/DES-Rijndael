package com.company.crypto.algorithm.impl;

import com.company.crypto.round.RoundKeysGenerator;
import com.company.crypto.round.RoundTransformer;

public class DES extends FeistelNetwork {
    public DES(RoundKeysGenerator roundKeysGenerator, RoundTransformer roundTransformer) {
        super(roundKeysGenerator, roundTransformer);
    }
}
