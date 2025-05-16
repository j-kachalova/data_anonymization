package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.io.Serializable;

public class AddressTransformStrategy implements AnonymizationStrategy, Serializable {

    @Override
    public String anonymize(String address) {
        if (address == null) return null;
        String[] parts = address.split(",");
        if (parts.length >= 2) {
            return parts[0].trim() + ", район";
        }
        return "РФ, регион";
    }
}
