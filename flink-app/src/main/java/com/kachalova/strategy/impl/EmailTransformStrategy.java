package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailTransformStrategy implements AnonymizationStrategy {

    public enum InvalidEmailAction {
        REJECT, PASSTHROUGH, NULL, GENERATE
    }

    public enum EmailType {
        UUID_V4, FULLNAME, RANDOM
    }

    private final boolean preserveDomain;
    private final boolean preserveLength;
    private final InvalidEmailAction invalidEmailAction;
    private final EmailType emailType;
    private final int maxLength;
    private final Random random = new Random();

    public EmailTransformStrategy(
            boolean preserveDomain,
            boolean preserveLength,
            InvalidEmailAction invalidEmailAction,
            EmailType emailType,
            int maxLength
    ) {
        this.preserveDomain = preserveDomain;
        this.preserveLength = preserveLength;
        this.invalidEmailAction = invalidEmailAction;
        this.emailType = emailType;
        this.maxLength = maxLength;
    }

    @Override
    public String anonymize(String email) {
        if (email == null || !isValidEmail(email)) {
            return handleInvalidEmail(email);
        }

        String[] parts = email.split("@");
        String domain = parts.length > 1 ? parts[1] : "";
        String newLocal = generateLocalPart();

        if (preserveLength) {
            newLocal = newLocal.substring(0, Math.min(newLocal.length(), parts[0].length()));
        }

        String result = preserveDomain ? newLocal + "@" + domain : newLocal + "@anon.com";
        return result.length() > maxLength ? result.substring(0, maxLength) : result;
    }

    private String handleInvalidEmail(String email) {
        switch (invalidEmailAction) {
            case PASSTHROUGH: return email;
            case NULL: return null;
            case GENERATE: return anonymize("generated@example.com");
            case REJECT: default: throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

    private String generateLocalPart() {
        return switch (emailType) {
            case UUID_V4 -> UUID.randomUUID().toString().replaceAll("-", "");
            case FULLNAME -> "user" + random.nextInt(10000);
            case RANDOM -> randomString(10);
        };
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String randomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

