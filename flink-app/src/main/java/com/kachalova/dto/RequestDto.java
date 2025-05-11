package com.kachalova.dto;



public class RequestDto {
    String phone;
    String email;
    public RequestDto() {

    }

    public RequestDto(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
