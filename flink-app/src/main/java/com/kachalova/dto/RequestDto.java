package com.kachalova.dto;



public class RequestDto {
    String message;
    public RequestDto() {

    }
    public RequestDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
