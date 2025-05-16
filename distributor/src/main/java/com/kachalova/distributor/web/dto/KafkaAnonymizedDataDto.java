package com.kachalova.distributor.web.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class KafkaAnonymizedDataDto {
    private String id;
    private String birthDate;
    private String birthPlace;
    private String passport;
    private String address;
    private String phone;
    private String email;
    private String inn;
    private String snils;
    private String card;
}
