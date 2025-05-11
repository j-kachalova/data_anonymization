package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

public class PhoneAnonymizationStrategy implements AnonymizationStrategy {

    @Override
    public String anonymize(String phone) {
        if (phone == null) return null;

        StringBuilder sb = new StringBuilder();
        int digitCount = 0;
        int totalDigits = phone.replaceAll("\\D", "").length();

        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (Character.isDigit(c)) {
                digitCount++;
                if (digitCount <= totalDigits - 2) {
                    sb.append('X');
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}

