package com.kachalova.distributor.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnonymizedDataDto {
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
