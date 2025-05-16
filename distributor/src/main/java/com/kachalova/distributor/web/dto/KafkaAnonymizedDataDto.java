package com.kachalova.distributor.web.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class KafkaAnonymizedDataDto {
    private String id;
    private String passport;
    private String phone;
    private String email;
}
