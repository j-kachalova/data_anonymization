package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.io.Serializable;
import java.util.Random;

public class CardTransformStrategy implements AnonymizationStrategy, Serializable {
    private static final Random RANDOM = new Random();

    @Override
    public String anonymize(String card) {
        if (card == null || card.length() < 4) return null;
        return "**--**-" + card.substring(card.length() - 4) + "-" + RANDOM.nextInt(99);
    }

}
