package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.io.Serializable;

public class PassportTransformStrategy implements AnonymizationStrategy, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public String anonymize(String passport) {
        if (passport == null || passport.length() < 6) {
            return "****-XXXXXX";
        }
        return "****-" + passport.substring(passport.length() - 6);
    }
}
