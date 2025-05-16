package com.kachalova.strategy;

import com.kachalova.dto.OriginalDataDto;
import com.kachalova.strategy.impl.*;

public class StrategySelector {

    public static AnonymizationStrategy addStrategy(String type) {
        AnonymizationStrategy strategy = switch (type) {
            case "email" -> new EmailTransformStrategy(true, false,
                    EmailTransformStrategy.InvalidEmailAction.GENERATE,
                    EmailTransformStrategy.EmailType.UUID_V4,
                    50);
            case "phone" -> new PhoneTransformStrategy(
                    PhoneTransformStrategy.Mode.GENERATE,
                    PhoneTransformStrategy.Format.STRING,
                    false,
                    0,
                    null
            );
            case "passport" -> new PassportTransformStrategy();
            case "birthDate" -> new BirthDateTransformStrategy();  // Пример
            case "birthPlace" -> new BirthPlaceTransformStrategy();  // Пример
            case "address" -> new AddressTransformStrategy();  // Пример
            case "inn" -> new INNTransformStrategy();  // Пример
            case "snils" -> new SnilsTransformStrategy();  // Пример
            case "card" -> new CardTransformStrategy();  // Пример
            default -> null;
        };
        return strategy;
    }

    public static String getValue(OriginalDataDto record, String type) {
        String value = switch (type) {
            case "email" -> record.getEmail();
            case "phone" -> record.getPhone();
            case "passport" -> record.getPassport();
            case "birthDate" -> record.getBirthDate();
            case "birthPlace" -> record.getBirthPlace();
            case "address" -> record.getAddress();
            case "inn" -> record.getInn();
            case "snils" -> record.getSnils();
            case "card" -> record.getCard();
            default -> null;
        };
        return value;
    }
}
