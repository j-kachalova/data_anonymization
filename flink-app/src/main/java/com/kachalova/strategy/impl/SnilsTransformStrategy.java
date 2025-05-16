package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.io.Serializable;

public class SnilsTransformStrategy implements AnonymizationStrategy, Serializable {
    @Override
    public String anonymize(String value) {
        return value;
    }
}
