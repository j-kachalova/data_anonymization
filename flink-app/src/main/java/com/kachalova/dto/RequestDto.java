package com.kachalova.dto;



public class RequestDto {
    String id;
    String phone;
    String email;
    public RequestDto() {

    }

    public RequestDto(String id, String phone, String email) {
        this.id = id;
        this.phone = phone;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
