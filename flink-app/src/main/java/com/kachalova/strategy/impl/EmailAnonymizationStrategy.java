package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

public class EmailAnonymizationStrategy implements AnonymizationStrategy {

    @Override
    public String anonymize(String email) {
        if (email == null || !email.contains("@")) {
            return "*****@unknown.com";
        }
        String[] parts = email.split("@");
        return "*****@" + parts[1]; // Сохраняем домен
    }
}
