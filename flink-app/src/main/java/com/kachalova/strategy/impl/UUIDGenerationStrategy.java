package com.kachalova.strategy.impl;
import com.kachalova.strategy.AnonymizationStrategy;

import java.util.UUID;

public class UUIDGenerationStrategy implements AnonymizationStrategy {
    @Override
    public String anonymize(String ignored) {
        return UUID.randomUUID().toString();
    }
}
