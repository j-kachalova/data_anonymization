package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

public class PassportTransformStrategy implements AnonymizationStrategy {

    @Override
    public String anonymize(String passport) {
        return "****-" + passport.substring(passport.length() - 6);
    }
}
