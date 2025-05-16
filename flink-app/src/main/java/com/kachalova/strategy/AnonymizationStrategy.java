package com.kachalova.strategy;

import java.io.Serializable;

public interface AnonymizationStrategy extends Serializable {
    String anonymize(String value);
}
