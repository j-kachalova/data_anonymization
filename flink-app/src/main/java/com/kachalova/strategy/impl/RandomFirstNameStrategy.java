package com.kachalova.strategy.impl;
import com.kachalova.strategy.AnonymizationStrategy;

import java.util.Random;

public class RandomFirstNameStrategy implements AnonymizationStrategy {
    private static final String[] NAMES = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
    private static final Random random = new Random();

    @Override
    public String anonymize(String ignored) {
        return NAMES[random.nextInt(NAMES.length)];
    }
}
