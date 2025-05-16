package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailTransformStrategy implements AnonymizationStrategy, Serializable{

    private static final long serialVersionUID = 1L;

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

    private transient Random random = new Random(); // transient: не сериализуется

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
        return switch (invalidEmailAction) {
            case PASSTHROUGH -> email;
            case NULL -> null;
            case GENERATE -> anonymize("generated@example.com");
            case REJECT -> throw new IllegalArgumentException("Invalid email: " + email);
        };
    }

    private String generateLocalPart() {
        ensureRandomInitialized();
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
        ensureRandomInitialized();
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void ensureRandomInitialized() {
        if (random == null) {
            random = new Random();
        }
    }
}
