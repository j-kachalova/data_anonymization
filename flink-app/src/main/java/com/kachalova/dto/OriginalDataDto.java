package com.kachalova.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginalDataDto implements Serializable {
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
