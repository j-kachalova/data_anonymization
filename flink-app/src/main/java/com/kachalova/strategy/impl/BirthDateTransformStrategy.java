package com.kachalova.strategy.impl;

import com.kachalova.strategy.AnonymizationStrategy;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BirthDateTransformStrategy implements AnonymizationStrategy, Serializable {
    @Override
    public String anonymize(String birthDate) {
        if (birthDate == null) return null;
        try {
            LocalDate date = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int decade = (date.getYear() / 10) * 10;
            return decade + "s";
        } catch (Exception e) {
            return "Unknown";
        }

    }
}
