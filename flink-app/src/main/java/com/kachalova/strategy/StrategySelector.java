package com.kachalova.strategy;

import com.kachalova.dto.OriginalDataDto;
import com.kachalova.strategy.impl.EmailTransformStrategy;
import com.kachalova.strategy.impl.PassportTransformStrategy;
import com.kachalova.strategy.impl.PhoneTransformStrategy;

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
            default -> null;
        };
        return strategy;
    }
    public static String getValue(OriginalDataDto record, String type){
        String value = switch (type) {
            case "email" -> record.getEmail();
            case "phone" -> record.getPhone();
            case "passport" -> record.getPassport();
            default -> null;
        };
        return value;
    }
}
