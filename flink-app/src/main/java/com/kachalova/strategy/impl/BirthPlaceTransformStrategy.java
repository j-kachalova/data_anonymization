package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.io.Serializable;

public class BirthPlaceTransformStrategy implements AnonymizationStrategy, Serializable {
    @Override
    public String anonymize(String birthPlace) {
        if (birthPlace == null) return null;
        if (birthPlace.toLowerCase().contains("область")) {
            return birthPlace.split(" ")[0] + " область";
        }
        return "РФ";
    }
}
