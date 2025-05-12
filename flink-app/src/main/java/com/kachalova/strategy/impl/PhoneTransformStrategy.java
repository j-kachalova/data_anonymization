package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.util.Random;

public class PhoneTransformStrategy implements AnonymizationStrategy {

    public enum Mode {
        MASK, GENERATE, GENERALIZE
    }

    public enum Format {
        E164, STRING
    }

    private final Mode mode;
    private final Format format;
    private final boolean preserveLastDigits;
    private final int preserveCount;
    private final String generalizationLabel;
    private final Random random = new Random();

    public PhoneTransformStrategy(Mode mode, Format format, boolean preserveLastDigits, int preserveCount, String generalizationLabel) {
        this.mode = mode;
        this.format = format;
        this.preserveLastDigits = preserveLastDigits;
        this.preserveCount = preserveCount;
        this.generalizationLabel = generalizationLabel;
    }

    @Override
    public String anonymize(String phone) {
        if (phone == null || phone.isBlank()) return null;

        String digits = phone.replaceAll("\\D", "");

        return switch (mode) {
            case MASK -> mask(digits);
            case GENERATE -> generate(digits.length());
            case GENERALIZE -> generalizationLabel;
        };
    }

    private String mask(String digits) {
        int preserve = preserveLastDigits ? Math.min(preserveCount, digits.length()) : 0;
        StringBuilder masked = new StringBuilder();

        for (int i = 0; i < digits.length() - preserve; i++) {
            masked.append("X");
        }
        masked.append(digits.substring(digits.length() - preserve));

        return formatPhone(masked.toString());
    }

    private String generate(int length) {
        StringBuilder gen = new StringBuilder();
        for (int i = 0; i < length; i++) {
            gen.append(random.nextInt(10));
        }
        return formatPhone(gen.toString());
    }

    private String formatPhone(String rawDigits) {
        if (format == Format.E164) {
            return "+" + rawDigits;
        } else {
            // (XXX) XXX-XX-XX
            if (rawDigits.length() >= 10) {
                return "(" + rawDigits.substring(0, 3) + ") " +
                        rawDigits.substring(3, 6) + "-" +
                        rawDigits.substring(6, 8) + "-" +
                        rawDigits.substring(8, Math.min(10, rawDigits.length()));
            } else {
                return rawDigits;
            }
        }
    }
}
