package com.kachalova.dto;

import java.io.Serializable;

public class OriginalDataDto implements Serializable {
    private String phone;
    private String email;

    public OriginalDataDto() {}

    public OriginalDataDto(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
