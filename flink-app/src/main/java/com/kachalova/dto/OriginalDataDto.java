package com.kachalova.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginalDataDto implements Serializable {
    private String passport;
    private String phone;
    private String email;
}
